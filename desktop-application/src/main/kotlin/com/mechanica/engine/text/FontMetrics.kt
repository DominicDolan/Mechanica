package com.mechanica.engine.text

import org.lwjgl.stb.STBTTFontinfo
import org.lwjgl.stb.STBTruetype
import org.lwjgl.system.MemoryStack

class FontMetrics(info: STBTTFontinfo, scale: Float = STBTruetype.stbtt_ScaleForPixelHeight(info, 1f)) {
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
            STBTruetype.stbtt_GetFontVMetrics(info, ascentBuffer, descentBuffer, lineGapBuffer)
            ascent = ascentBuffer[0]
            descent = descentBuffer[0]
            lineGap = lineGapBuffer[0]
        }

        this.ascent = ascent*scale
        this.descent = descent*scale
        this.lineGap = lineGap*scale
    }
}