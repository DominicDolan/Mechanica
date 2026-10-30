package com.mechanica.engine.text

import com.cave.library.vector.vec2.MutableVector2
import com.mechanica.engine.shaders.text.Font
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype
import org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

class  LwjglStandardFont(resource: ByteBuffer, initializer: FontAtlasConfiguration.() -> Unit): Font() {

    override val ascent: Float
        get() = metrics.ascent
    override val descent: Float
        get() = metrics.descent
    override val lineGap: Float
        get() = metrics.lineGap

    private val config = LwjglFontAtlasConfiguration(initializer)
    private val charRange = config.charRange.first.code..config.charRange.last.code
    private val unknownIndex = when {
        '?'.code in charRange -> '?'.code - charRange.first
        else -> 0
    }

    private val data = FontData(resource)

    override val atlas = FontAtlas(data, config)

    override val distanceFieldEdge: Float?
        get() = atlas.distanceFieldEdge

    private val metrics = FontMetrics(data.info, data.scale)

    override fun addCharacterDataToArrays(cursor: CharacterCursor, positions: Array<MutableVector2>, texCoords: Array<MutableVector2>) {
        val c = cursor.currentChar
        val atlasScale = atlas.scale
        val dataScale = data.scale
        
        val charAsInt = if (c.code in charRange) c.code - charRange.first else unknownIndex
        val kern = if (cursor.previousChar.code in charRange) {
            stbtt_GetCodepointKernAdvance(data.info, cursor.previousChar.code, charRange.first + charAsInt) / atlasScale.toFloat()
        } else 0f
        
        MemoryStack.stackPush().use { stack ->
            val x = stack.floats(cursor.xAdvance/(dataScale*atlasScale).toFloat() + kern)
            // Line advance is applied in copyToArrays in normalized font units.
            val y = stack.floats(0f)
            val q = STBTTAlignedQuad.malloc(stack)

            // Atlas-pixel rounding causes spacing to change with the chosen bake size.
            STBTruetype.stbtt_GetPackedQuad(data.cdata, atlas.width, atlas.height, charAsInt, x, y, q, false)
            cursor.xAdvance = (x[0]*dataScale*atlasScale).toFloat()

            if (c != ' ') q.copyToArrays(cursor, dataScale*atlasScale, atlas, positions, texCoords)
        }
    }

    inner class FontData(val ttf: ByteBuffer) {
        val info: STBTTFontinfo = STBTTFontinfo.create()
        val cdata: STBTTPackedchar.Buffer
        val scale: Float

        init {
            require(ttf.remaining() >= 12) { "Font resource is empty or shorter than a font header" }
            check(STBTruetype.stbtt_InitFont(info, ttf)) { "Failed to initialize font information." }
            cdata = STBTTPackedchar.create(charRange.count())
            scale = STBTruetype.stbtt_ScaleForPixelHeight(info, 1f)
        }
    }

}
