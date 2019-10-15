package util.colors

import util.extensions.degrees
import util.extensions.isHigher
import util.units.Angle
import kotlin.math.max
import kotlin.math.min

inline class HexColor(private val hex: Long): Color {
    override val a: Double
        get() = hex2Alpha(hex)
    override val r: Double
        get() = hex2Red(hex)
    override val g: Double
        get() = hex2Green(hex)
    override val b: Double
        get() = hex2Blue(hex)
    override val hue: Angle
        get() {
            var X = 0.0
            var Y = 0.0
            var C = 0.0
            val max = max(max(g , b), r)
            val min = min(min(g , b), r)

            when {
                r == max -> {
                    X = g
                    Y = b
                }
                g == max -> {
                    C = 2.0
                    X = g
                    Y = b
                }
                b == max -> {
                    C = 4.0
                    X = r
                    Y = g
                }
            }
            return if (max -min == 0.0) {
                0.degrees
            } else ((C + (X -Y)/(max - min))*60).degrees
        }

    override fun toLong() = hex

    override fun toString(): String {
        return String.format("0x%08X", hex)
    }
}