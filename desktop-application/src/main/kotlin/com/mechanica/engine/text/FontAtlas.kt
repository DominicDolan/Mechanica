package com.mechanica.engine.text

import com.mechanica.engine.memory.BitmapBuffer
import com.mechanica.engine.memory.useMemoryStack
import com.mechanica.engine.shader.LwjglImage
import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.util.extensions.constrain
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype
import java.nio.ByteBuffer
import java.nio.IntBuffer
import kotlin.math.*

class FontAtlas(private val data: LwjglStandardFont.FontData, private val config: LwjglFontAtlasConfiguration) : Image {
    private val image: Image

    val width: Int = config.width
    val height: Int = config.height

    override val id: Int
        get() = image.id

    val scale: Double

    private val distanceFieldStart: Double
    private val distanceFieldEnd: Double

    private val distanceFieldPadding: Double
        get() = max(distanceFieldStart, distanceFieldEnd)
    val padding: Float
        get() = config.padding + if (config.distanceFieldsWasSet) distanceFieldPadding.toFloat() else 0f

    private val charRange = config.charRange.first.toInt()..config.charRange.last.toInt()
    private val unknownIndex = 128 - charRange.first

    init {
        val bitmap = BufferUtils.createByteBuffer(width * height)

        val correction = distanceFieldCorrection()
        distanceFieldStart = config.sdfConfiguration.start + correction
        distanceFieldEnd = config.sdfConfiguration.end + correction

        packFont(bitmap, data.ttf, data.cdata)

        if (config.distanceFieldsWasSet) {
            insertCharacters(bitmap)
        }

        scale = getScale(data)

        image = LwjglImage.create(bitmap, width, height, 1, GL11.GL_ALPHA)
    }

    private fun insertCharacters(bitmap: ByteBuffer) {
        val bitmapBuffer = BitmapBuffer(bitmap, width, height)
        for (char in charRange){
            val glyph = createSDFGlyph(char.toChar())

            val charAsInt = if (char in charRange) char - charRange.first else unknownIndex
            val paddingVector = vec(padding/width.toFloat(), padding/height.toFloat())

            if (glyph != null) {
                useMemoryStack {
                    val q = STBTTAlignedQuad.mallocStack(this)
                    STBTruetype.stbtt_GetPackedQuad(data.cdata, width, height, charAsInt, floats(0f), floats(0f), q, false)
                    bitmapBuffer.insert(glyph, q.s0() - paddingVector.x.toFloat(), q.t0() - paddingVector.y.toFloat())
                }
            }
        }
        bitmap.position(0)
    }

    private fun getScale(data: LwjglStandardFont.FontData): Double {
        val c = config.charRange.first
        val x = data.cdata[0].xadvance()
        return getAtlasScale(data.info, c, if (x >= 1f) x else 1f)
    }

    override fun bind() {
        image.bind()
    }

    private fun packFont(bitmap: ByteBuffer, ttf: ByteBuffer, cdata: STBTTPackedchar.Buffer) {
        val characterHeight = config.characterSize

        STBTTPackContext.malloc().use { pc ->
            STBTruetype.stbtt_PackBegin(pc, bitmap, width, height, 0, padding.toInt()*2)

            val success = STBTruetype.stbtt_PackFontRange(pc, ttf, 0, characterHeight, charRange.first, cdata)

            if (!success) throw IllegalStateException("Error packing font into font atlas. The provided character size, ${config.characterSize}," +
                        " is likely too big for the provided atlas size, ${width}x$height")

            STBTruetype.stbtt_PackEnd(pc)
        }

        config.characterSize = characterHeight
    }

    private fun checkPadding() {
        val area = calculateAllowedCharacterArea()
        val side = sqrt(area.toDouble())
        val paddingArea = padding*4*(side - padding)
        val characterArea = area - paddingArea

        if (characterArea <= 0) throw IllegalStateException("Error packing font into font atlas. The provided padding, $padding," +
                " is too much for the provided atlas size, ${width}x$height")

    }

    private fun calculateAllowedCharacterArea(): Int {
        val bitmapArea = width*height
        return bitmapArea/charRange.count()
    }

    private fun distanceFieldCorrection(): Double {
        val start = config.sdfConfiguration.start
        val end = config.sdfConfiguration.end

        return if (start*end > 0) {
            val sign = sign(start)
            -sign*min(abs(start), abs(end))
        } else 0.0
    }

    private fun createSDFGlyph(char: Char): BitmapBuffer? {
        var start = distanceFieldStart
        var end = distanceFieldEnd

        if (start*end > 0) {
            val sign = sign(start)
            val min = sign*min(abs(start), abs(end))
            start -= min
            end -= min
        }

        val edgeValue = getOnEdgeValue(start, end)

        val pixelDistScale = 255f/(end - start).toFloat()

        useMemoryStack {
            val width: IntBuffer = ints(0)
            val height: IntBuffer = ints(0)
            val scale = STBTruetype.stbtt_ScaleForPixelHeight(data.info, config.characterSize)

            val buffer = STBTruetype.stbtt_GetCodepointSDF(data.info, scale, char.toInt(), distanceFieldPadding.toInt(), edgeValue.toByte(), pixelDistScale,
                    width, height, ints(0), ints(0))

            if (buffer != null) return BitmapBuffer(buffer, width[0], height[0])
        }

        return null
    }

    private fun getOnEdgeValue(start: Double, end: Double): Int {
        val startRatio = start/(end - start)
        return ((1.0 + startRatio)*255).constrain(0.0, 255.0).toInt()
    }

}