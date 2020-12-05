package com.mechanica.engine.color

import com.mechanica.engine.unit.angle.Degree

interface Color {
    val a: Double
    val r: Double
    val g: Double
    val b: Double

    val hue: Degree
        get() = rgb2Hue(r, g, b)
    val saturation: Double
        get() = rgb2Saturation(r, g, b)
    val lightness: Double
        get() = rgb2Lightness(r, g, b)

    operator fun get(index: Int): Double {
        return when (index) {
            0 -> r
            1 -> g
            2 -> b
            3 -> a
            else -> throw ArrayIndexOutOfBoundsException("Tried to get a fifth component from this color")
        }
    }

    fun getOrZero(index: Int): Double {
        return when (index) {
            0 -> r
            1 -> g
            2 -> b
            3 -> a
            else -> 0.0
        }
    }

    fun toLong(): Long

    companion object {
        fun rgba(r: Double, g: Double, b: Double, a: Double): InlineColor = com.mechanica.engine.color.rgba(r, g, b, a)
        fun hex(hex: Long): InlineColor = com.mechanica.engine.color.hex(hex)
        fun hsl(hue: Degree, saturation: Double, lightness: Double, alpha: Double = 1.0): InlineColor
                = com.mechanica.engine.color.hsl(hue, saturation, lightness, alpha)

        val red = rgba(1.0, 0.0, 0.0, 1.0)
        val green = rgba(0.0, 1.0, 0.0, 1.0)
        val blue = rgba(0.0, 0.0, 1.0, 1.0)

        val black = rgba(0.0, 0.0, 0.0, 1.0)
        val white = rgba(1.0, 1.0, 1.0, 1.0)
    }
}