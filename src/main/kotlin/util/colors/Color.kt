package util.colors

import util.units.Angle

interface Color {
    val a: Double
    val r: Double
    val g: Double
    val b: Double
    val hue: Angle

    fun toLong(): Long
}