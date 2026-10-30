@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.cave.library.vector.vec2.MutableVector2
import com.mechanica.engine.resources.defaultFont
import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.shaders.text.Font
import com.mechanica.engine.shaders.text.FontFactory
import com.mechanica.engine.text.CharacterCursor
import com.mechanica.engine.text.FontAtlasConfiguration
import com.mechanica.nativebackend.stb.*
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.roundToInt
import platform.posix.memset

class NativeFontFactory : FontFactory {
    override fun font(ttfFile: ByteArray, initializer: FontAtlasConfiguration.() -> Unit): Font = NativeFont(ttfFile, initializer)
    override fun defaultFont(): Font = Font.defaultFont
}

private class NativeFontConfig(initializer: FontAtlasConfiguration.() -> Unit) : FontAtlasConfiguration {
    override var width = 1024
    override var height = 1024
    override var characterSize = 48f
    private var paddingSet = false
    private var paddingValue = 8f
    override var padding: Float
        get() = if (sdf && !paddingSet) 2f else paddingValue
        set(value) { paddingValue = value; paddingSet = true }
    override var charRange = ' '..128.toChar()
    var sdf = false
    val sdfConfig = FontAtlasConfiguration.SDFConfiguration()
    override fun configureSDF(block: FontAtlasConfiguration.SDFConfiguration.() -> Unit) {
        sdf = true
        block(sdfConfig)
    }
    init {
        initializer(this)
        require(width in 1..32767 && height in 1..32767)
        require(characterSize.isFinite() && characterSize > 0f)
        require(padding.isFinite() && padding >= 1f && padding <= minOf(width, height) / 2f)
        require(!charRange.isEmpty())
        if (sdf) require(sdfConfig.start.isFinite() && sdfConfig.end.isFinite() &&
            sdfConfig.weightAdjustment.isFinite() && sdfConfig.start < 0.0 && sdfConfig.end > 0.0)
    }
}

private class NativeFont(ttf: ByteArray, initializer: FontAtlasConfiguration.() -> Unit) : Font() {
    private val config = NativeFontConfig(initializer)
    private val range = config.charRange.first.code..config.charRange.last.code
    private val fallback = if ('?'.code in range) '?'.code - range.first else 0
    private val fontBytes: CPointer<UByteVar>
    private val info: CPointer<stbtt_fontinfo>
    private val packed: CPointer<stbtt_packedchar>
    private val scale: Float
    private val atlasScale: Double
    override val ascent: Float
    override val descent: Float
    override val lineGap: Float
    override val distanceFieldEdge: Float?
    override val atlas: Image

    init {
        require(ttf.size >= 12) { "Font data is too short" }
        fontBytes = nativeHeap.allocArray(ttf.size)
        ttf.forEachIndexed { index, byte -> fontBytes[index] = byte.toUByte() }
        info = nativeHeap.alloc<stbtt_fontinfo>().ptr
        packed = nativeHeap.allocArray(range.count())
        memset(packed, 0, (sizeOf<stbtt_packedchar>() * range.count()).toULong())
        try {
            check(stbtt_InitFont(info, fontBytes, 0) != 0) { "Invalid font" }
            scale = stbtt_ScaleForPixelHeight(info, 1f)
            atlasScale = 1.0 / (scale * config.characterSize)
            val metrics = memScoped {
                val a = alloc<IntVar>(); val d = alloc<IntVar>(); val g = alloc<IntVar>()
                stbtt_GetFontVMetrics(info, a.ptr, d.ptr, g.ptr)
                Triple(a.value * scale, d.value * scale, g.value * scale)
            }
            ascent = metrics.first; descent = metrics.second; lineGap = metrics.third
            val pixels = ByteArray(config.width * config.height)
            val edge = if (config.sdf) {
                val sdf = config.sdfConfig
                ((sdf.end / (sdf.end - sdf.start) * 255).toInt().coerceIn(1, 254)) / 255f
            } else null
            distanceFieldEdge = edge?.let {
                val sdf = config.sdfConfig
                (it - (sdf.weightAdjustment / (sdf.end - sdf.start)).toFloat()).also { adjusted ->
                    require(adjusted > 0f && adjusted < 1f) { "SDF weight is outside the encoded distance range" }
                }
            }
            if (edge == null) packBitmap(pixels) else packSdf(pixels, edge)
            atlas = uploadAtlas(pixels)
        } catch (error: Throwable) {
            nativeHeap.free(packed); nativeHeap.free(info); nativeHeap.free(fontBytes)
            throw error
        }
    }

    private fun packBitmap(pixels: ByteArray) = memScoped {
        val context = alloc<stbtt_pack_context>()
        pixels.usePinned {
            check(stbtt_PackBegin(context.ptr, it.addressOf(0).reinterpret(), config.width, config.height, 0, ceil(config.padding * 2).toInt(), null) != 0) { "Unable to initialize font packing" }
            try {
                check(stbtt_PackFontRange(context.ptr, fontBytes, 0, config.characterSize, range.first, range.count(), packed) != 0) { "Font glyphs do not fit in the atlas" }
            } finally { stbtt_PackEnd(context.ptr) }
        }
    }

    private fun packSdf(pixels: ByteArray, edge: Float) = memScoped {
        val sdf = config.sdfConfig
        val fontScale = stbtt_ScaleForPixelHeight(info, config.characterSize)
        val sdfPadding = ceil(maxOf(-sdf.start, sdf.end)).toInt()
        val gutter = ceil(config.padding).toInt()
        require(sdfPadding > 0 && sdfPadding <= minOf(config.width, config.height) / 2)
        val onEdge = (edge * 255).roundToInt().toUByte()
        val distanceScale = (255.0 / (sdf.end - sdf.start)).toFloat()
        val w = alloc<IntVar>(); val h = alloc<IntVar>(); val ox = alloc<IntVar>(); val oy = alloc<IntVar>()
        val advance = alloc<IntVar>(); val bearing = alloc<IntVar>()
        val x0 = alloc<IntVar>(); val y0 = alloc<IntVar>(); val x1 = alloc<IntVar>(); val y1 = alloc<IntVar>()
        var x = gutter; var y = gutter; var rowHeight = 0
        for (code in range) {
            val target = packed[code - range.first]
            stbtt_GetCodepointHMetrics(info, code, advance.ptr, bearing.ptr)
            target.xadvance = advance.value * fontScale
            stbtt_GetCodepointBitmapBox(info, code, fontScale, fontScale, x0.ptr, y0.ptr, x1.ptr, y1.ptr)
            if (x1.value <= x0.value || y1.value <= y0.value) continue
            val glyph = checkNotNull(stbtt_GetCodepointSDF(info, fontScale, code, sdfPadding, onEdge, distanceScale, w.ptr, h.ptr, ox.ptr, oy.ptr)) { "Unable to generate glyph $code" }
            try {
                if (x + w.value + gutter > config.width) { x = gutter; y += rowHeight; rowHeight = 0 }
                check(x + w.value + gutter <= config.width && y + h.value + gutter <= config.height) { "Font glyphs do not fit in the atlas" }
                for (row in 0 until h.value) for (col in 0 until w.value) pixels[(y + row) * config.width + x + col] = glyph[row * w.value + col].toByte()
                target.x0 = x.toUShort(); target.y0 = y.toUShort()
                target.x1 = (x + w.value).toUShort(); target.y1 = (y + h.value).toUShort()
                target.xoff = ox.value.toFloat(); target.yoff = oy.value.toFloat()
                target.xoff2 = (ox.value + w.value).toFloat(); target.yoff2 = (oy.value + h.value).toFloat()
                x += w.value + gutter * 2
                rowHeight = maxOf(rowHeight, h.value + gutter * 2)
            } finally { stbtt_FreeSDF(glyph, null) }
        }
    }

    private fun uploadAtlas(pixels: ByteArray): Image = memScoped {
        val id = alloc<UIntVar>(); glGenTextures(1, id.ptr)
        check(id.value != 0u) { "Unable to create font texture" }
        glBindTexture(GL_TEXTURE_2D.toUInt(), id.value)
        val alignment = alloc<IntVar>(); glGetIntegerv(GL_UNPACK_ALIGNMENT.toUInt(), alignment.ptr)
        try {
            glPixelStorei(GL_UNPACK_ALIGNMENT.toUInt(), 1)
            pixels.usePinned { glTexImage2D(GL_TEXTURE_2D.toUInt(), 0, GL_R8, config.width, config.height, 0, GL_RED.toUInt(), GL_UNSIGNED_BYTE.toUInt(), it.addressOf(0)) }
            glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_SWIZZLE_R.toUInt(), GL_ONE)
            glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_SWIZZLE_G.toUInt(), GL_ONE)
            glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_SWIZZLE_B.toUInt(), GL_ONE)
            glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_SWIZZLE_A.toUInt(), GL_RED)
            glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_WRAP_S.toUInt(), GL_CLAMP_TO_EDGE)
            glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_WRAP_T.toUInt(), GL_CLAMP_TO_EDGE)
            val mips = if (config.sdf) 0 else minOf(floor(log2(config.padding.toDouble())).toInt(), floor(log2(minOf(config.width, config.height).toDouble())).toInt())
            glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_MAX_LEVEL.toUInt(), mips)
            glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_MAG_FILTER.toUInt(), GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_MIN_FILTER.toUInt(), if (mips == 0) GL_LINEAR else GL_LINEAR_MIPMAP_LINEAR)
            if (mips > 0) glGenerateMipmap(GL_TEXTURE_2D.toUInt())
        } finally { glPixelStorei(GL_UNPACK_ALIGNMENT.toUInt(), alignment.value) }
        val textureId = id.value.toInt()
        object : Image {
            override val id = textureId
            override fun bind() = glBindTexture(GL_TEXTURE_2D.toUInt(), textureId.toUInt())
        }
    }

    override fun addCharacterDataToArrays(cursor: CharacterCursor, positions: Array<MutableVector2>, texCoords: Array<MutableVector2>) = memScoped {
        val c = cursor.currentChar
        val index = if (c.code in range) c.code - range.first else fallback
        val kern = if (cursor.previousChar.code in range) stbtt_GetCodepointKernAdvance(info, cursor.previousChar.code, range.first + index) / atlasScale.toFloat() else 0f
        val x = alloc<FloatVar>(); x.value = cursor.xAdvance / (scale * atlasScale).toFloat() + kern
        val y = alloc<FloatVar>(); y.value = 0f
        val q = alloc<stbtt_aligned_quad>()
        stbtt_GetPackedQuad(packed, config.width, config.height, index, x.ptr, y.ptr, q.ptr, 0)
        val factor = scale * atlasScale
        cursor.xAdvance = (x.value * factor).toFloat()
        if (c != ' ') {
            val i = maxOf(cursor.nonWhiteSpaceIndex - 1, 0) * 4
            val padding = 1.0; val tx = padding / config.width; val ty = padding / config.height
            texCoords[i].set(q.s0.toDouble() - tx, q.t1.toDouble() + ty)
            texCoords[i + 1].set(q.s1.toDouble() + tx, q.t1.toDouble() + ty)
            texCoords[i + 2].set(q.s0.toDouble() - tx, q.t0.toDouble() - ty)
            texCoords[i + 3].set(q.s1.toDouble() + tx, q.t0.toDouble() - ty)
            positions[i].set((q.x0 - padding) * factor, (-q.y1 - padding) * factor + cursor.yAdvance)
            positions[i + 1].set((q.x1 + padding) * factor, (-q.y1 - padding) * factor + cursor.yAdvance)
            positions[i + 2].set((q.x0 - padding) * factor, (-q.y0 + padding) * factor + cursor.yAdvance)
            positions[i + 3].set((q.x1 + padding) * factor, (-q.y0 + padding) * factor + cursor.yAdvance)
        }
    }
}
