package com.mechanica.engine.color

class DynamicColor(
        override var r: Double,
        override var g: Double,
        override var b: Double,
        override var a: Double) : Color {

    fun set(red: Double, green: Double, blue: Double, alpha: Double = 1.0) {
        r = red
        g = green
        b = blue
        a = alpha
    }

    fun set(color: Color) {
        set(color.r, color.g, color.b, color.a)
    }

    fun set(color: LightweightColor) {
        set(color.r, color.g, color.b, color.a)
    }

    override fun toLong(): Long {
        return rgba2Hex(r, g, b, a)
    }
}