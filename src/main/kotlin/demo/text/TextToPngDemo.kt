package demo.text

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBImageWrite
import org.lwjgl.stb.STBTTBakedChar
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack
import resources.Res
import java.nio.ByteBuffer

fun main() {
    val demo = TextToPngDemo("Roboto-Regular.ttf")

    val text = "The quick brown fox jumped over the lazy dog"
    demo.saveText(text)
}

private class TextToPngDemo(fontName: String) {

    val info: STBTTFontinfo

    val ascent: Int
    val descent: Int
    val lineGap: Int

    val fontHeight = 24

    val width = 512
    val height = 128

    val bitmap: ByteBuffer

    init {
        val ttf = Res.font[fontName].buffer
        info = STBTTFontinfo.create()
        check(STBTruetype.stbtt_InitFont(info, ttf)) { "Failed to initialize font information." }

        var ascent = 0
        var descent = 0
        var lineGap = 0

        MemoryStack.stackPush().use { stack ->
            val pAscent = stack.mallocInt(1)
            val pDescent = stack.mallocInt(1)
            val pLineGap = stack.mallocInt(1)
            STBTruetype.stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap)
            ascent = pAscent[0]
            descent = pDescent[0]
            lineGap = pLineGap[0]
        }
        this.ascent = ascent
        this.descent = descent
        this.lineGap = lineGap

        bitmap = BufferUtils.createByteBuffer(width * height)


    }

    fun saveText(text: String) {

        val scale = STBTruetype.stbtt_ScaleForPixelHeight(info, fontHeight.toFloat())

        val ascent = (ascent*scale).toInt()
        val descent = (descent*scale).toInt()
        val lineGap = (lineGap*scale).toInt()


        var x = 0
        for (c in text.indices){
            MemoryStack.stackPush().use { stack ->
                val character = text[c].toInt()

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

                x += (ax*scale).toInt()

                /* add kerning */
                if (c < text.length - 1) {
                    val kern = STBTruetype.stbtt_GetCodepointKernAdvance(info, character, text[c + 1].toInt())
                    x += (kern * scale).toInt()
                }
            }
        }

        bitmap.position(0)
        STBImageWrite.stbi_write_png("out.png", width, height, 1, bitmap, width)
    }
}