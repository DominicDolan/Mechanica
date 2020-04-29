package com.mechanica.engine.color

import com.mechanica.engine.unit.angle.Angle

inline class LightweightColor(private val hex: Long): Color {
    override val a: Double
        get() = hex2Alpha(hex)
    override val r: Double
        get() = hex2Red(hex)
    override val g: Double
        get() = hex2Green(hex)
    override val b: Double
        get() = hex2Blue(hex)
    override val hue: Angle
        get() = hex2Hue(hex)

    override fun toLong() = hex

    override fun toString(): String {
        return String.format("0x%08X", hex)
    }
}