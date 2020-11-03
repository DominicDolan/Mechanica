package com.mechanica.engine.color

import com.mechanica.engine.unit.angle.Degree

interface DynamicColor : Color {
    override var r: Double
    override var g: Double
    override var b: Double
    override var a: Double

    fun set(red: Double = this.r, green: Double = this.g, blue: Double = this.b, alpha: Double = this.a) {
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

    companion object {
        fun rgba(r: Double, g: Double, b: Double, a: Double): DynamicColor {
            return object : DynamicColor {
                override var r: Double = r
                override var g: Double = g
                override var b: Double = b
                override var a: Double = a

            }
        }

        fun from(color: Color) = rgba(color.r, color.g, color.b, color.a)
        fun from(color: InlineColor) = rgba(color.r, color.g, color.b, color.a)

        fun create() = rgba(1.0, 1.0, 1.0, 1.0)

        fun hex(hex: Long): DynamicColor = from(com.mechanica.engine.color.hex(hex))
        fun hsl(hue: Degree, saturation: Double, lightness: Double, alpha: Double = 1.0): DynamicColor
                = from(com.mechanica.engine.color.hsl(hue, saturation, lightness, alpha))
    }
}