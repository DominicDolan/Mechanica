package demo.text

import display.Display
import gl.utils.loadImage
import graphics.Image
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.*
import org.lwjgl.system.MemoryStack
import resources.Resource
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.abs

class Font(resource: Resource) {
    val atlas: Image

    private val width = 1024
    private val height = 1024

    private val fontHeight: Float
    private val capacity = 96
//    private val

    private val ttf: ByteBuffer = resource.buffer
    private val info: STBTTFontinfo = STBTTFontinfo.create()

    private val ascent: Int
    private val descent: Int
    private val lineGap: Int

    private val cdata: STBTTBakedChar.Buffer

    private val character: CharacterCoordinates = CharacterCoordinates()

    init {
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

        cdata = STBTTBakedChar.malloc(capacity)

        val bitmap = BufferUtils.createByteBuffer(width * height)
        fontHeight = bakeFont(bitmap)

        atlas = loadImage(bitmap, width, height, 4, GL11.GL_ALPHA)
        character.reset()
    }

    fun loadChar(c: Char) = character.calculate(c)

    fun resetCursor() {
        character.reset()
    }

    private fun bakeFont(bitmap: ByteBuffer): Float {
        var check: Int
        var attempts = 0
        var maxFontHeight = 96f
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
                    error = -0.3f*(1f + (check.toFloat())/capacity.toFloat())
                } else if (check > 0) {
                    error = 0.8f*(1f - check.toFloat()/height.toFloat())
                }

                checkedFontHeight *= (1f + error)
                attempts++
                continue
            } else {
                break
            }
        }
        STBTruetype.stbtt_BakeFontBitmap(ttf, maxFontHeight, bitmap, width, height, 32, cdata)
        return maxFontHeight
    }

    inner class CharacterCoordinates {

        val positions = FloatArray(3*4)
        val texCoords = FloatArray(2*4)

        var x: FloatBuffer = FloatBuffer.allocate(1)
        var y: FloatBuffer = FloatBuffer.allocate(1)
        var q: STBTTAlignedQuad = STBTTAlignedQuad(ByteBuffer.allocate(8*4))

        init {
            reset()
        }

        fun calculate(c: Char): CharacterCoordinates {
            STBTruetype.stbtt_GetBakedQuad(cdata, width, height, c.toInt() - 32, x, y, q, true)
            copyToFloats(q, 0f, 0f)
            return this
        }

        fun reset() {
            MemoryStack.stackPush().use { stack ->
                x = stack.floats(0.0f)
                y = stack.floats(0.0f)
                q = STBTTAlignedQuad.mallocStack(stack)
            }
        }

        fun copyToFloats(q: STBTTAlignedQuad, x: Float, y: Float) {

            val scale = STBTruetype.stbtt_ScaleForPixelHeight(info, fontHeight)

            texCoords[0] = q.s0()
            texCoords[1] = q.t1()
            texCoords[2] = q.s1()
            texCoords[3] = q.t1()
            texCoords[4] = q.s0()
            texCoords[5] = q.t0()
            texCoords[6] = q.s1()
            texCoords[7] = q.t0()

            positions[0] = (q.x0() + x)*scale* Display.contentScaleX
            positions[1] = (-q.y1() + y)*scale* Display.contentScaleY
            positions[3] = (q.x1() + x)*scale* Display.contentScaleX
            positions[4] = (-q.y1() + y)*scale* Display.contentScaleY
            positions[6] = (q.x0() + x)*scale* Display.contentScaleX
            positions[7] = (-q.y0() + y)*scale* Display.contentScaleY
            positions[9] = (q.x1() + x)*scale* Display.contentScaleX
            positions[10] = (-q.y0() + y)*scale* Display.contentScaleY
        }
    }

}