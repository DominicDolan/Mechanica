package temp

import TruetypeOversample
import com.mechanica.engine.context.GLContext
import com.mechanica.engine.context.GLInitializer
import com.mechanica.engine.context.loader.LwjglLoader
import com.mechanica.engine.display.Window
import com.mechanica.engine.memory.useMemoryStack
import com.mechanica.engine.resources.Resource
import org.lwjgl.BufferUtils
import org.lwjgl.stb.*
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer


fun main() {
    GLContext.initialize(Window.Companion.create("test", 100, 100))
    GLInitializer.initialize(LwjglLoader())

    val demo = TextToPngDemo("Roboto-Regular.ttf")

    val text = "The quick brown fox jumped over the lazy dog"
    demo.packCharacters()
//    STBTruetype.stbtt_PackBegin(ctx)

}

private class TextToPngDemo(fontName: String) {

    val info: STBTTFontinfo = STBTTFontinfo.create()

    val ascent: Int
    val descent: Int
    val lineGap: Int

    val fontHeight = 24

    val width = 512
    val height = 512

    val bitmap: ByteBuffer
    val ttf: ByteBuffer = Resource("res/fonts/$fontName").buffer

    init {
        check(stbtt_InitFont(info, ttf)) { "Failed to initialize font information." }

        var ascent = 0
        var descent = 0
        var lineGap = 0

        MemoryStack.stackPush().use { stack ->
            val pAscent = stack.mallocInt(1)
            val pDescent = stack.mallocInt(1)
            val pLineGap = stack.mallocInt(1)
            stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap)
            ascent = pAscent[0]
            descent = pDescent[0]
            lineGap = pLineGap[0]
        }
        this.ascent = ascent
        this.descent = descent
        this.lineGap = lineGap

        bitmap = BufferUtils.createByteBuffer(width * height)

    }

    fun packCharacters() {
        val cdata = STBTTPackedchar.malloc(128)

        STBTTPackContext.malloc().use { pc ->
            stbtt_PackBegin(pc, bitmap, width, height, 0, 20)
            stbtt_PackFontRange(pc, ttf, 0, 32f, 32, cdata)
            stbtt_PackEnd(pc)
        }
        STBImageWrite.stbi_write_png("packed.png", width, height, 1, bitmap, width)

        useMemoryStack {
            val xb = floats(0f)
            val yb = floats(0f)
            val q = STBTTAlignedQuad.malloc()
            stbtt_GetPackedQuad(cdata, width, height, ')'.toInt() - 32, xb, yb, q, false)
            println("p0: ${q.x0()}, ${q.y0()}")
            println("p1: ${q.x1()}, ${q.y1()}")
            println("t0: ${q.s0()}, ${q.t0()}")
            println("t1: ${q.s1()}, ${q.t1()}")
            q.free()
        }
        cdata.free()
    }

    // Calling these functions in sequence is roughly equivalent to calling
    // stbtt_PackFontRanges(). If you more control over the packing of multiple
    // fonts, or if you want to pack custom data into a font texture, take a look
    // at the source to of stbtt_PackFontRanges() and create a custom version
    // using these functions, e.g. call GatherRects multiple times,
    // building up a single array of rects, then call PackRects once,
    // then call RenderIntoRects repeatedly. This may result in a
    // better packing than calling PackFontRanges multiple times
    // (or it may not).
    fun packCharactersIndividually() {
        val capacity = 95
        val cdata = STBTTPackedchar.calloc(capacity)
//        cdata.limit(32 + 95)
//        cdata.position(32)
//        stbtt_PackFontRange(pc, ttf, 0, 24f, 32, cdata)
        val range = STBTTPackRange.calloc(1)
        range.first_unicode_codepoint_in_range(32)

        range.num_chars(cdata.remaining())
        range.chardata_for_range(cdata)
        range.font_size(24f)

        STBTTPackContext.malloc().use { pc ->
            stbtt_PackBegin(pc, bitmap, width, height, 0, 1)

            val rects = STBRPRect.malloc(capacity)

            stbtt_PackFontRangesGatherRects(pc, info, range, rects)

            stbtt_PackFontRangesPackRects(pc, rects)

            stbtt_PackFontRangesRenderIntoRects(pc, info, range, rects)

            stbtt_PackEnd(pc)
            rects.free()
        }
        cdata.free()
        range.free()
        STBImageWrite.stbi_write_png("packed.png", width, height, 1, bitmap, width)
    }

    fun saveAllCharacters() {

        var x = 0
        for (i in 'A'.toInt()..'z'.toInt()){
            x = addCharacterToBitmap(i.toChar(), bitmap, x)
        }

        bitmap.position(0)
        STBImageWrite.stbi_write_png("out.png", width, height, 1, bitmap, width)
    }

    fun saveText(text: String) {

        var x = 0
        for (c in text){
            x = addCharacterToBitmap(c, bitmap, x)
        }

        bitmap.position(0)
        STBImageWrite.stbi_write_png("out.png", width, height, 1, bitmap, width)
    }

    private fun addCharacterToBitmap(c: Char, bitmap: ByteBuffer, x: Int): Int {

        val scale = stbtt_ScaleForPixelHeight(info, fontHeight.toFloat())

        val ascent = (ascent*scale).toInt()
        val descent = (descent*scale).toInt()
        val lineGap = (lineGap*scale).toInt()


        var xAdvance = 0
        MemoryStack.stackPush().use { stack ->
            val character = c.toInt()

            val axBuffer = stack.mallocInt(1)
            val lsbBuffer = stack.mallocInt(1)
            STBTruetype.stbtt_GetCodepointHMetrics(info, character, axBuffer, lsbBuffer)

            val ax = axBuffer[0]
            val lsb = lsbBuffer[0]

            val x1 = stack.mallocInt(1);
            val x2 = stack.mallocInt(1)
            val y1 = stack.mallocInt(1);
            val y2 = stack.mallocInt(1)

            STBTruetype.stbtt_GetCodepointBitmapBox(info, character, scale, scale, x1, y1, x2, y2)

            val y: Int = ascent + y1[0]

            val byteOffset: Int = x + (lsb * scale + y * width.toLong()).toInt()

            STBTruetype.stbtt_MakeCodepointBitmap(info, bitmap.position(byteOffset), x2[0] - x1[0], y2[0] - y1[0], width, scale, scale, character)

            xAdvance = x + (ax*scale).toInt()

            /* add kerning */
//            if (c < text.length - 1) {
//                val kern = STBTruetype.stbtt_GetCodepointKernAdvance(info, character, text[c + 1].toInt())
//                x += (kern * scale).toInt()
//            }
        }

        return xAdvance
    }
}