package com.mechanica.engine.text

import com.mechanica.engine.models.Image
import com.mechanica.engine.resources.Resource
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.utils.loadImage
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTBakedChar
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import java.nio.IntBuffer
import kotlin.math.max

class LwjglFont(resource: Resource): Font() {

    val quadScale: Double
    override val atlas: Image
    val scale: Float
    override val ascent: Float
    override val descent: Float
    override val lineGap: Float

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

        cdata = STBTTBakedChar.create(charRange.count())

        val bitmap = BufferUtils.createByteBuffer(atlasWidth * atlasHeight)
        bakeFont(bitmap, ttf, cdata)

        quadScale = getBakedQuadScale(info, cdata)

        scale = STBTruetype.stbtt_ScaleForPixelHeight(info, 1f)

        this.ascent = ascent*scale
        this.descent = descent*scale
        this.lineGap = lineGap*scale

        atlas = loadImage(bitmap, atlasWidth, atlasHeight, 1, GL11.GL_ALPHA)
    }

    override fun addCharacterDataToArrays(cursor: CharacterCursor, positions: Array<DynamicVector>, texCoords: Array<DynamicVector>) {
        val c = cursor.currentChar
        
        val kern = stbtt_GetCodepointKernAdvance(info, cursor.previousChar.toInt(), c.toInt()).toFloat()/quadScale.toFloat()
        val charAsInt = if (c.toInt() in charRange) c.toInt() - charRange.first else unknownIndex
        
        MemoryStack.stackPush().use { stack ->
            val x = stack.floats(cursor.xAdvance/(scale*quadScale).toFloat() + kern)
            val y = stack.floats(cursor.yAdvance)
            val q = STBTTAlignedQuad.mallocStack(stack)

            STBTruetype.stbtt_GetBakedQuad(cdata, atlasWidth, atlasHeight, charAsInt, x, y, q, true)
            cursor.xAdvance = (x[0]*scale*quadScale).toFloat()

            if (c != ' ') copyToFloats(q, cursor, positions, texCoords)
        }
    }

    private fun copyToFloats(q: STBTTAlignedQuad, cursor: CharacterCursor, p: Array<DynamicVector>, tc: Array<DynamicVector>) {
        val i = max(cursor.nonWhiteSpaceIndex - 1, 0)*4
        tc[i + 0].x = q.s0().toDouble()
        tc[i + 0].y = q.t1().toDouble()
        tc[i + 1].x = q.s1().toDouble()
        tc[i + 1].y = q.t1().toDouble()
        tc[i + 2].x = q.s0().toDouble()
        tc[i + 2].y = q.t0().toDouble()
        tc[i + 3].x = q.s1().toDouble()
        tc[i + 3].y = q.t0().toDouble()

        val yAdvance = cursor.yAdvance
        p[i + 0].x = (q.x0())*scale*quadScale
        p[i + 0].y = (-q.y1())*scale*quadScale + yAdvance
        p[i + 1].x = (q.x1())*scale*quadScale
        p[i + 1].y = (-q.y1())*scale*quadScale + yAdvance
        p[i + 2].x = (q.x0())*scale*quadScale
        p[i + 2].y = (-q.y0())*scale*quadScale + yAdvance
        p[i + 3].x = (q.x1())*scale*quadScale
        p[i + 3].y = (-q.y0())*scale*quadScale + yAdvance
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


    private fun getBakedQuadScale(fontInfo: STBTTFontinfo, bakedFontData: STBTTBakedChar.Buffer): Double {
        MemoryStack.stackPush().use {stack ->
            val xAdvanceBuffer: IntBuffer = stack.mallocInt(1)
            val lsbBuffer: IntBuffer = stack.mallocInt(1)
            STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, charRange.first, xAdvanceBuffer, lsbBuffer)

            val xFromBakedData = bakedFontData[0].xadvance()
            val xFromFontInfo = xAdvanceBuffer[0]

            return xFromFontInfo.toDouble()/xFromBakedData
        }
    }

    protected fun finalize() {
        cdata.free()
    }

    companion object {

        private val charRange = 32..128
        private val unknownIndex = 128 - charRange.first

        private const val atlasWidth = 1024
        private const val atlasHeight = 1024

    }
}