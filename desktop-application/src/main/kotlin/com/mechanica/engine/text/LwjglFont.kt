package com.mechanica.engine.text

import com.mechanica.engine.resources.Resource
import com.mechanica.engine.unit.vector.DynamicVector
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTBakedChar
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import org.lwjgl.stb.STBTruetype.stbtt_GetCodepointKernAdvance
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import kotlin.math.max

class LwjglFont(resource: Resource): Font() {

    override val ascent: Float
        get() = metrics.ascent
    override val descent: Float
        get() = metrics.descent
    override val lineGap: Float
        get() = metrics.lineGap

    private val data = FontData(resource)
    override val atlas = FontAtlas(data)

    private val metrics = FontMetrics()

    override fun addCharacterDataToArrays(cursor: CharacterCursor, positions: Array<DynamicVector>, texCoords: Array<DynamicVector>) {
        val c = cursor.currentChar
        val atlasScale = atlas.scale
        val dataScale = data.scale
        
        val kern = stbtt_GetCodepointKernAdvance(data.info, cursor.previousChar.toInt(), c.toInt()).toFloat()/atlasScale.toFloat()
        val charAsInt = if (c.toInt() in charRange) c.toInt() - charRange.first else unknownIndex
        
        MemoryStack.stackPush().use { stack ->
            val x = stack.floats(cursor.xAdvance/(dataScale*atlasScale).toFloat() + kern)
            val y = stack.floats(cursor.yAdvance)
            val q = STBTTAlignedQuad.mallocStack(stack)

            STBTruetype.stbtt_GetBakedQuad(data.cdata, atlas.width, atlas.height, charAsInt, x, y, q, true)
            cursor.xAdvance = (x[0]*dataScale*atlasScale).toFloat()

            if (c != ' ') copyToFloats(q, cursor, positions, texCoords)
        }
    }

    private fun copyToFloats(q: STBTTAlignedQuad, cursor: CharacterCursor, p: Array<DynamicVector>, tc: Array<DynamicVector>) {
        val i = max(cursor.nonWhiteSpaceIndex - 1, 0)*4
        val atlasScale = atlas.scale
        val dataScale = data.scale

        tc[i + 0].x = q.s0().toDouble()
        tc[i + 0].y = q.t1().toDouble()
        tc[i + 1].x = q.s1().toDouble()
        tc[i + 1].y = q.t1().toDouble()
        tc[i + 2].x = q.s0().toDouble()
        tc[i + 2].y = q.t0().toDouble()
        tc[i + 3].x = q.s1().toDouble()
        tc[i + 3].y = q.t0().toDouble()

        val yAdvance = cursor.yAdvance
        p[i + 0].x = (q.x0())*dataScale*atlasScale
        p[i + 0].y = (-q.y1())*dataScale*atlasScale + yAdvance
        p[i + 1].x = (q.x1())*dataScale*atlasScale
        p[i + 1].y = (-q.y1())*dataScale*atlasScale + yAdvance
        p[i + 2].x = (q.x0())*dataScale*atlasScale
        p[i + 2].y = (-q.y0())*dataScale*atlasScale + yAdvance
        p[i + 3].x = (q.x1())*dataScale*atlasScale
        p[i + 3].y = (-q.y0())*dataScale*atlasScale + yAdvance
    }

    inner class FontData(resource: Resource) {
        val ttf: ByteBuffer = resource.buffer
        val info: STBTTFontinfo = STBTTFontinfo.create()
        val cdata: STBTTBakedChar.Buffer
        val scale: Float

        init {
            check(STBTruetype.stbtt_InitFont(info, ttf)) { "Failed to initialize font information." }
            cdata = STBTTBakedChar.create(charRange.count())
            scale = STBTruetype.stbtt_ScaleForPixelHeight(info, 1f)
        }
    }

    private inner class FontMetrics {
        val ascent: Float
        val descent: Float
        val lineGap: Float

        init {
            var ascent = 0
            var descent = 0
            var lineGap = 0

            MemoryStack.stackPush().use { stack ->
                val ascentBuffer = stack.mallocInt(1)
                val descentBuffer = stack.mallocInt(1)
                val lineGapBuffer = stack.mallocInt(1)
                STBTruetype.stbtt_GetFontVMetrics(data.info, ascentBuffer, descentBuffer, lineGapBuffer)
                ascent = ascentBuffer[0]
                descent = descentBuffer[0]
                lineGap = lineGapBuffer[0]
            }

            this.ascent = ascent*data.scale
            this.descent = descent*data.scale
            this.lineGap = lineGap*data.scale
        }
    }
    companion object {

        val charRange = 32..128
        private val unknownIndex = 128 - charRange.first

    }
}