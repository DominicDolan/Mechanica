package com.mechanica.engine.text

import com.mechanica.engine.unit.vector.DynamicVector
import org.lwjgl.stb.STBTTAlignedQuad
import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack
import java.nio.IntBuffer
import kotlin.math.max

fun STBTTAlignedQuad.copyToArrays(cursor: CharacterCursor, scale: Double, atlas: FontAtlas, p: Array<DynamicVector>, tc: Array<DynamicVector>) {
    val i = max(cursor.nonWhiteSpaceIndex - 1, 0) *4

    val padding = atlas.padding*1.01
    val texPaddingX = padding/atlas.width.toDouble()
    val texPaddingY = padding/atlas.height.toDouble()

    tc[i + 0].x = s0().toDouble() - texPaddingX
    tc[i + 0].y = t1().toDouble() + texPaddingY
    tc[i + 1].x = s1().toDouble() + texPaddingX
    tc[i + 1].y = t1().toDouble() + texPaddingY
    tc[i + 2].x = s0().toDouble() - texPaddingX
    tc[i + 2].y = t0().toDouble() - texPaddingY
    tc[i + 3].x = s1().toDouble() + texPaddingX
    tc[i + 3].y = t0().toDouble() - texPaddingY

    val yAdvance = cursor.yAdvance
    p[i + 0].x = (x0() - padding)*scale
    p[i + 0].y = (-y1() - padding)*scale + yAdvance
    p[i + 1].x = (x1() + padding)*scale
    p[i + 1].y = (-y1() - padding)*scale + yAdvance
    p[i + 2].x = (x0() - padding)*scale
    p[i + 2].y = (-y0() + padding)*scale + yAdvance
    p[i + 3].x = (x1() + padding)*scale
    p[i + 3].y = (-y0() + padding)*scale + yAdvance
}


fun getAtlasScale(fontInfo: STBTTFontinfo, testChar: Char, testCharXAdvance: Float): Double {
    MemoryStack.stackPush().use { stack ->
        val xAdvanceBuffer: IntBuffer = stack.mallocInt(1)
        val lsbBuffer: IntBuffer = stack.mallocInt(1)
        STBTruetype.stbtt_GetCodepointHMetrics(fontInfo, testChar.toInt(), xAdvanceBuffer, lsbBuffer)

        val xFromFontInfo = xAdvanceBuffer[0]

        return xFromFontInfo.toDouble()/ testCharXAdvance
    }
}
