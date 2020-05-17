package com.mechanica.engine.text

import com.mechanica.engine.models.Image
import com.mechanica.engine.utils.loadImage
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTBakedChar
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import java.nio.IntBuffer

class FontAtlas(data: LwjglFont.FontData, val width: Int = 1024, val height: Int = 1024) : Image {
    private val image: Image

    override val id: Int
        get() = image.id

    val scale: Double

    init {
        val bitmap = BufferUtils.createByteBuffer(width * height)
        bakeFont(bitmap, data.ttf, data.cdata)
        scale = getBakedQuadScale(data.info, data.cdata)

        image = loadImage(bitmap, width, height, 1, GL11.GL_ALPHA)
    }

    override fun bind() {
        image.bind()
    }

    private fun bakeFont(bitmap: ByteBuffer, ttf: ByteBuffer, cdata: STBTTBakedChar.Buffer): Float {
        var check: Int
        var attempts = 0
        var maxFontHeight = 100f
        var maxRow = 0
        var checkedFontHeight = maxFontHeight

        while (true) {
            check = STBTruetype.stbtt_BakeFontBitmap(ttf, checkedFontHeight, bitmap, width, height, 32, cdata)
            if (check == 0) { maxFontHeight = checkedFontHeight; break }

            if (attempts < 5) {
                if (check > maxRow) {
                    maxRow = check
                    maxFontHeight = checkedFontHeight
                }

                var error = 0f
                if (check < 0) {
                    error = -0.3f*(1f + (check.toFloat())/ LwjglFont.charRange.count().toFloat())
                } else if (check > 0) {
                    error = 0.8f*(1f - check.toFloat()/ height.toFloat())
                }

                checkedFontHeight *= (1f + error)
                attempts++
                continue
            } else {
                break
            }
        }
        STBTruetype.stbtt_BakeFontBitmap(ttf, maxFontHeight, bitmap, width, height, LwjglFont.charRange.first, cdata)
        return maxFontHeight
    }


    private fun getBakedQuadScale(fontInfo: STBTTFontinfo, bakedFontData: STBTTBakedChar.Buffer): Double {
        MemoryStack.stackPush().use { stack ->
            val xAdvanceBuffer: IntBuffer = stack.mallocInt(1)
            val lsbBuffer: IntBuffer = stack.mallocInt(1)
            STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, LwjglFont.charRange.first, xAdvanceBuffer, lsbBuffer)

            val xFromBakedData = bakedFontData[0].xadvance()
            val xFromFontInfo = xAdvanceBuffer[0]

            return xFromFontInfo.toDouble()/xFromBakedData
        }
    }

}