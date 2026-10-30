package com.mechanica.engine.text

import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import kotlin.math.roundToInt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FontAtlasBitmapTest {
    private val ttf: ByteBuffer = javaClass.getResourceAsStream("/res/fonts/roboto/Roboto-Regular.ttf")!!
        .use { it.readBytes() }.let { bytes -> BufferUtils.createByteBuffer(bytes.size).put(bytes).flip() }
    private val info = STBTTFontinfo.create().also { check(stbtt_InitFont(it, ttf)) }

    @Test
    fun `bitmap atlases contain antialiased coverage and gutter limited mips`() {
        for ((padding, levels) in listOf(1f to 0, 2f to 1, 8f to 3)) {
            val config = LwjglFontAtlasConfiguration { this.padding = padding }
            val chars = STBTTPackedchar.create(config.charRange.count())
            val atlas = FontAtlasBitmap(ttf, info, chars, config)
            assertEquals(levels, atlas.mipLevels)
            assertNull(atlas.distanceFieldEdge)
            assertTrue((0 until atlas.pixels.capacity()).any { (atlas.pixels[it].toInt() and 255) in 1..254 })
            assertTrue(chars['A'.code - config.charRange.first.code].xadvance() > 0f)
        }
    }

    @Test
    fun `SDF atlas stores complete glyphs with original bearings and no overlaps`() {
        // Exercise a custom range that has no legacy character-128 fallback, plus
        // asymmetric distances so a hardcoded shader edge of 0.5 would fail.
        val config = LwjglFontAtlasConfiguration {
            charRange = ' '..'z'
            width = 1024
            height = 512
            configureSDF { start = -3.0; end = 7.0 }
        }
        val chars = STBTTPackedchar.create(config.charRange.count())
        val atlas = FontAtlasBitmap(ttf, info, chars, config)
        assertEquals(0, atlas.mipLevels)
        assertEquals(178f / 255f, atlas.distanceFieldEdge)
        val edge = (atlas.distanceFieldEdge!! * 255).roundToInt()
        val scale = stbtt_ScaleForPixelHeight(info, config.characterSize)
        val occupied = BooleanArray(config.width * config.height)
        MemoryStack.stackPush().use { stack ->
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val x = stack.mallocInt(1)
            val y = stack.mallocInt(1)
            for ((index, char) in config.charRange.withIndex()) {
                val packed = chars[index]
                val glyph = stbtt_GetCodepointSDF(info, scale, char.code, 7, edge.toByte(), 25.5f, w, h, x, y)
                if (glyph == null) {
                    assertEquals(0, packed.x1() - packed.x0())
                    continue
                }
                try {
                    assertEquals(x[0].toFloat(), packed.xoff(), "x bearing of $char")
                    assertEquals(y[0].toFloat(), packed.yoff(), "y bearing of $char")
                    assertEquals(w[0].toFloat(), packed.xoff2() - packed.xoff())
                    assertEquals(h[0].toFloat(), packed.yoff2() - packed.yoff())
                    val left = packed.x0().toInt()
                    val top = packed.y0().toInt()
                    for (row in 0 until h[0]) for (col in 0 until w[0]) {
                        val offset = (top + row) * config.width + left + col
                        assertTrue(!occupied[offset], "glyph $char overlaps another glyph")
                        occupied[offset] = true
                        assertEquals(glyph[row * w[0] + col], atlas.pixels[offset], "pixel of $char at $col,$row")
                    }
                } finally {
                    stbtt_FreeSDF(glyph, 0L)
                }
            }
        }
        // Every pixel not owned by a glyph must remain transparent, including the atlas edges.
        for (i in occupied.indices) if (!occupied[i]) assertEquals(0, atlas.pixels[i].toInt())
        assertTrue(chars[0].xadvance() > 0f, "space retains its advance")
    }

    @Test
    fun `bitmap and SDF metrics agree despite different padding`() {
        val bitmapConfig = LwjglFontAtlasConfiguration { }
        val sdfConfig = LwjglFontAtlasConfiguration { configureSDF { } }
        val bitmapChars = STBTTPackedchar.create(bitmapConfig.charRange.count())
        val sdfChars = STBTTPackedchar.create(sdfConfig.charRange.count())
        FontAtlasBitmap(ttf, info, bitmapChars, bitmapConfig)
        FontAtlasBitmap(ttf, info, sdfChars, sdfConfig)
        for (i in 0 until bitmapChars.capacity()) {
            assertEquals(bitmapChars[i].xadvance(), sdfChars[i].xadvance(), 0.0001f)
        }
    }

    @Test
    fun `packing overflow fails before writing glyphs outside the atlas`() {
        val config = LwjglFontAtlasConfiguration {
            width = 32; height = 32
            characterSize = 48f
            configureSDF { }
        }
        assertFailsWith<IllegalStateException> {
            FontAtlasBitmap(ttf, info, STBTTPackedchar.create(config.charRange.count()), config)
        }
    }

    @Test
    fun `invalid atlas settings fail with an argument error`() {
        assertFailsWith<IllegalArgumentException> { LwjglFontAtlasConfiguration { characterSize = Float.NaN } }
        assertFailsWith<IllegalArgumentException> { LwjglFontAtlasConfiguration { padding = 0f } }
        assertFailsWith<IllegalArgumentException> { LwjglFontAtlasConfiguration { charRange = 'z'..'a' } }
        assertFailsWith<IllegalArgumentException> { LwjglFontAtlasConfiguration { configureSDF { start = 0.0 } } }
        assertFailsWith<IllegalArgumentException> { LwjglFontAtlasConfiguration { configureSDF { end = Double.POSITIVE_INFINITY } } }
    }

    @Test
    fun `SDF weight moves the rendered contour without changing atlas pixels or advances`() {
        val regularConfig = LwjglFontAtlasConfiguration { configureSDF { } }
        val heavyConfig = LwjglFontAtlasConfiguration { configureSDF { weightAdjustment = 0.6 } }
        val regularChars = STBTTPackedchar.create(regularConfig.charRange.count())
        val heavyChars = STBTTPackedchar.create(heavyConfig.charRange.count())
        val regular = FontAtlasBitmap(ttf, info, regularChars, regularConfig)
        val heavy = FontAtlasBitmap(ttf, info, heavyChars, heavyConfig)
        assertEquals(regular.pixels, heavy.pixels, "weight must not be baked into the distance map")
        assertEquals(regular.distanceFieldEdge, heavy.distanceFieldEdge)
        assertEquals(regular.renderDistanceFieldEdge!! - 0.6f / 8f, heavy.renderDistanceFieldEdge!!, 0.00001f)
        assertTrue(heavy.renderDistanceFieldEdge!! < regular.renderDistanceFieldEdge!!)
        for (i in 0 until regularChars.capacity()) {
            assertEquals(regularChars[i].xadvance(), heavyChars[i].xadvance())
            assertEquals(regularChars[i].xoff(), heavyChars[i].xoff())
            assertEquals(regularChars[i].yoff(), heavyChars[i].yoff())
        }
    }

    @Test
    fun `weight cannot move the contour beyond the available distance map`() {
        val config = LwjglFontAtlasConfiguration { configureSDF { weightAdjustment = 8.0 } }
        assertFailsWith<IllegalArgumentException> {
            FontAtlasBitmap(ttf, info, STBTTPackedchar.create(config.charRange.count()), config)
        }
        assertFailsWith<IllegalArgumentException> {
            LwjglFontAtlasConfiguration { configureSDF { weightAdjustment = Double.NaN } }
        }
    }

    @Test
    fun `empty font resources are rejected before calling native code`() {
        assertFailsWith<IllegalArgumentException> { LwjglStandardFont(BufferUtils.createByteBuffer(0)) { } }
    }
}
