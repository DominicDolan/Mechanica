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

    fun set(color: InlineColor) {
        set(color.r, color.g, color.b, color.a)
    }

    fun set(hex: Long) {
        val r = hex2Red(hex)
        val g = hex2Green(hex)
        val b = hex2Blue(hex)
        val a = hex2Alpha(hex)
        set(r, g, b, a)
    }

    override fun toLong(): Long {
        return rgba2Hex(r, g, b, a)
    }
}