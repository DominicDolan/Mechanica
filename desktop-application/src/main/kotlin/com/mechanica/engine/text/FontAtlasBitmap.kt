package com.mechanica.engine.text

import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBRPContext
import org.lwjgl.stb.STBRPNode
import org.lwjgl.stb.STBRPRect
import org.lwjgl.stb.STBRectPack
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max
import kotlin.math.roundToInt

/** CPU atlas construction, independent of OpenGL so packing and bearings can be tested. */
internal class FontAtlasBitmap(
    ttf: ByteBuffer,
    info: STBTTFontinfo,
    characters: STBTTPackedchar.Buffer,
    private val config: LwjglFontAtlasConfiguration,
) {
    val pixels: ByteBuffer = BufferUtils.createByteBuffer(Math.multiplyExact(config.width, config.height))

    // Averaging encoded distances is not the same as averaging coverage. SDFs use
    // linear sampling at level zero and reconstruct screen-space coverage in the shader.
    // Bitmap mips stop while each filter footprint still fits in the glyph gutter.
    val mipLevels: Int = if (config.distanceFieldsWasSet) 0 else minOf(
        floor(log2(config.padding.toDouble())).toInt(),
        floor(log2(minOf(config.width, config.height).toDouble())).toInt(),
    )

    val distanceFieldEdge: Float? = if (config.distanceFieldsWasSet) {
        val sdf = config.sdfConfiguration
        // STB encodes zero distance as a byte, so the shader must use the same quantized value.
        ((sdf.end / (sdf.end - sdf.start) * 255).toInt().coerceIn(1, 254)) / 255f
    } else null

    val renderDistanceFieldEdge: Float? = distanceFieldEdge?.let { edge ->
        val sdf = config.sdfConfiguration
        val adjusted = edge - (sdf.weightAdjustment / (sdf.end - sdf.start)).toFloat()
        require(adjusted > 0f && adjusted < 1f) { "SDF weight adjustment must stay inside the encoded distance range" }
        adjusted
    }

    init {
        if (config.distanceFieldsWasSet) packSdf(info, characters) else packBitmap(ttf, characters)
    }

    private fun packBitmap(ttf: ByteBuffer, characters: STBTTPackedchar.Buffer) {
        STBTTPackContext.malloc().use { context ->
            check(stbtt_PackBegin(context, pixels, config.width, config.height, 0, ceil(config.padding * 2).toInt())) {
                "Could not initialize font atlas packing"
            }
            try {
                check(stbtt_PackFontRange(context, ttf, 0, config.characterSize, config.charRange.first.code, characters)) {
                    packingError()
                }
            } finally {
                stbtt_PackEnd(context)
            }
        }
    }

    private fun packSdf(info: STBTTFontinfo, characters: STBTTPackedchar.Buffer) {
        val sdf = config.sdfConfiguration
        val scale = stbtt_ScaleForPixelHeight(info, config.characterSize)
        val sdfPadding = ceil(max(-sdf.start, sdf.end)).toInt()
        require(sdfPadding > 0 && sdfPadding <= minOf(config.width, config.height) / 2) {
            "SDF distance range is too large for the atlas"
        }
        val gutter = ceil(config.padding).toInt()
        val edge = (distanceFieldEdge!! * 255).roundToInt()
        val distanceScale = (255.0 / (sdf.end - sdf.start)).toFloat()

        STBRPRect.calloc(characters.capacity()).use { rects ->
            MemoryStack.stackPush().use { stack ->
                val x0 = stack.mallocInt(1)
                val y0 = stack.mallocInt(1)
                val x1 = stack.mallocInt(1)
                val y1 = stack.mallocInt(1)
                val advance = stack.mallocInt(1)
                val bearing = stack.mallocInt(1)
                for ((index, char) in config.charRange.withIndex()) {
                    stbtt_GetCodepointHMetrics(info, char.code, advance, bearing)
                    val packed = characters[index]
                    packed.xadvance(advance[0] * scale)
                    stbtt_GetCodepointBitmapBox(info, char.code, scale, scale, x0, y0, x1, y1)
                    val w = x1[0] - x0[0]
                    val h = y1[0] - y0[0]
                    if (w <= 0 || h <= 0) continue // space has an advance but no image
                    rects[index].w(w + 2 * (sdfPadding + gutter)).h(h + 2 * (sdfPadding + gutter))
                }
            }
            STBRPContext.malloc().use { context ->
                STBRPNode.malloc(config.width).use { nodes ->
                    STBRectPack.stbrp_init_target(context, config.width, config.height, nodes)
                    check(STBRectPack.stbrp_pack_rects(context, rects) != 0) { packingError() }
                }
            }
            MemoryStack.stackPush().use { stack ->
                val width = stack.mallocInt(1)
                val height = stack.mallocInt(1)
                val xOffset = stack.mallocInt(1)
                val yOffset = stack.mallocInt(1)
                for ((index, char) in config.charRange.withIndex()) {
                    val rect = rects[index]
                    if (rect.w() == 0 || rect.h() == 0) continue
                    val glyph = stbtt_GetCodepointSDF(info, scale, char.code, sdfPadding, edge.toByte(), distanceScale,
                        width, height, xOffset, yOffset) ?: error("Could not generate SDF for character ${char.code}")
                    try {
                        check(width[0] + 2 * gutter == rect.w() && height[0] + 2 * gutter == rect.h()) {
                            "SDF glyph dimensions did not match its packed rectangle"
                        }
                        val x = rect.x() + gutter
                        val y = rect.y() + gutter
                        for (row in 0 until height[0]) {
                            pixels.put((y + row) * config.width + x, glyph, row * width[0], width[0])
                        }
                        characters[index]
                            .x0(x.toShort()).y0(y.toShort())
                            .x1((x + width[0]).toShort()).y1((y + height[0]).toShort())
                            .xoff(xOffset[0].toFloat()).yoff(yOffset[0].toFloat())
                            .xoff2((xOffset[0] + width[0]).toFloat()).yoff2((yOffset[0] + height[0]).toFloat())
                    } finally {
                        stbtt_FreeSDF(glyph, 0L)
                    }
                }
            }
        }
    }

    private fun packingError() = "Font glyphs at ${config.characterSize}px with padding do not fit in " +
        "the ${config.width}x${config.height} atlas; increase the atlas dimensions or reduce characterSize / padding"
}
