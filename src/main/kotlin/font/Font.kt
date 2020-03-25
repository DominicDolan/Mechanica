package font

import gl.utils.loadImage
import graphics.Image
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTBakedChar
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack
import resources.Resource
import util.extensions.restrain
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Font(resource: Resource) {
    val atlas: Image

    val scale: Float
    val quadScale: Float

    val ascent: Float
    val descent: Float
    val lineGap: Float
    val lineHeight: Float
        get() = ascent - descent+lineGap

    private val character: CharacterCoordinates = CharacterCoordinates()

    private val info: STBTTFontinfo = STBTTFontinfo.create()
    private val ttf: ByteBuffer = resource.buffer
    private val cdata: STBTTBakedChar.Buffer

    init {
        check(STBTruetype.stbtt_InitFont(info, ttf)) { "Failed to initialize font information." }

        var ascent = 0
        var descent = 0
        var lineGap = 0

        MemoryStack.stackPush().use { stack ->
            val ascentBuffer = stack.mallocInt(1)
            val descentBuffer = stack.mallocInt(1)
            val lineGapBuffer = stack.mallocInt(1)
            STBTruetype.stbtt_GetFontVMetrics(info, ascentBuffer, descentBuffer, lineGapBuffer)
            ascent = ascentBuffer[0]
            descent = descentBuffer[0]
            lineGap = lineGapBuffer[0]
        }

        cdata = STBTTBakedChar.malloc(charRange.count())

        val bitmap = BufferUtils.createByteBuffer(atlasWidth * atlasHeight)
        bakeFont(bitmap, ttf, cdata)

        quadScale = getBakedQuadScale(info, cdata)

        scale = STBTruetype.stbtt_ScaleForPixelHeight(info, 1f)

        this.ascent = ascent*scale
        this.descent = descent*scale
        this.lineGap = lineGap*scale

        atlas = loadImage(bitmap, atlasWidth, atlasHeight, 4, GL11.GL_ALPHA)
    }

    fun alignedQuadAsFloats(c: Char, coords: CharacterCoordinates): CharacterCoordinates {
        MemoryStack.stackPush().use { stack ->
            val kern = getKernAdvance(coords.previousChar, c)/quadScale
            val x = stack.floats(coords.xAdvance + kern)
            val y = stack.floats(coords.yAdvance)
            val q = STBTTAlignedQuad.mallocStack(stack)

            val unknownIndex = 128 - charRange.first
            val charIndex = if (c.toInt() in charRange) c.toInt() - charRange.first
                            else unknownIndex

            STBTruetype.stbtt_GetBakedQuad(cdata, atlasWidth, atlasHeight, charIndex, x, y, q, true)

            return character.copyToFloats(c, q, x)
        }
    }

    fun resetCharacterCoordinates(): CharacterCoordinates {
        character.reset()
        return character
    }

    fun getKernAdvance(c1: Char, c2: Char): Float {
        return STBTruetype.stbtt_GetCodepointKernAdvance(info, c1.toInt(), c2.toInt()).toFloat()
    }

    private fun bakeFont(bitmap: ByteBuffer, ttf: ByteBuffer, cdata: STBTTBakedChar.Buffer): Float {
        var check: Int
        var attempts = 0
        var maxFontHeight = 100f
        var maxRow = 0
        var checkedFontHeight = maxFontHeight

        while (true) {
            check = STBTruetype.stbtt_BakeFontBitmap(ttf, checkedFontHeight, bitmap, atlasWidth, atlasHeight, 32, cdata)
            if (check == 0) { maxFontHeight = checkedFontHeight; break }

            if (attempts < 5) {
                if (check > maxRow) {
                    maxRow = check
                    maxFontHeight = checkedFontHeight
                }

                var error = 0f
                if (check < 0) {
                    error = -0.3f*(1f + (check.toFloat())/ charRange.count().toFloat())
                } else if (check > 0) {
                    error = 0.8f*(1f - check.toFloat()/ atlasHeight.toFloat())
                }

                checkedFontHeight *= (1f + error)
                attempts++
                continue
            } else {
                break
            }
        }
        STBTruetype.stbtt_BakeFontBitmap(ttf, maxFontHeight, bitmap, atlasWidth, atlasHeight, charRange.first, cdata)
        return maxFontHeight
    }


    private fun getBakedQuadScale(fontInfo: STBTTFontinfo, bakedFontData: STBTTBakedChar.Buffer): Float {
        MemoryStack.stackPush().use {stack ->
            val xBuffer: IntBuffer = stack.mallocInt(1)
            val lsbBuffer: IntBuffer = stack.mallocInt(1)
            STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, charRange.first, xBuffer, lsbBuffer)

            val xFromBakedData = bakedFontData[0].xadvance()
            val xFromFontInfo = xBuffer[0]

            return xFromFontInfo/xFromBakedData
        }
    }

    inner class CharacterCoordinates {

        val positions = FloatArray(3*4)
        val texCoords = FloatArray(2*4)
        var xAdvance = 0f
        var yAdvance = 0f
        var previousChar = ' '

        fun copyToFloats(c: Char, q: STBTTAlignedQuad, xBuffer: FloatBuffer): CharacterCoordinates {

            texCoords[0] = q.s0()
            texCoords[1] = q.t1()
            texCoords[2] = q.s1()
            texCoords[3] = q.t1()
            texCoords[4] = q.s0()
            texCoords[5] = q.t0()
            texCoords[6] = q.s1()
            texCoords[7] = q.t0()

            positions[0] = (q.x0())*scale*quadScale
            positions[1] = (-q.y1())*scale*quadScale + yAdvance
            positions[3] = (q.x1())*scale*quadScale
            positions[4] = (-q.y1())*scale*quadScale + yAdvance
            positions[6] = (q.x0())*scale*quadScale
            positions[7] = (-q.y0())*scale*quadScale + yAdvance
            positions[9] = (q.x1())*scale*quadScale
            positions[10] = (-q.y0())*scale*quadScale + yAdvance

            previousChar = c
            xAdvance = xBuffer[0]
            return this
        }

        fun reset() {
            character.xAdvance = 0f
            character.yAdvance = 0f
            character.positions.fill(0f)
            character.texCoords.fill(0f)
            character.previousChar = ' '
        }
    }

    companion object {
        private val charRange = 32..128

        private const val atlasWidth = 1024
        private const val atlasHeight = 1024

    }
}