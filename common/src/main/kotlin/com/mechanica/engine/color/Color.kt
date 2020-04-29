package com.mechanica.engine.color

import com.mechanica.engine.unit.angle.Angle

interface Color {
    val a: Double
    val r: Double
    val g: Double
    val b: Double

    val hue: Angle
        get() = rgb2Hue(r, g, b)
    val saturation: Double
        get() = rgb2Saturation(r, g, b)
    val lightness: Double
        get() = rgb2Lightness(r, g, b)

    fun toLong(): Long
}