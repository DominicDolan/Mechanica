package unitTests.color

import util.colors.Color
import util.colors.hex
import util.colors.hsl
import kotlin.test.Test

class ColorTest {
    @Test fun colorConversions() {
        val hexadecimals = arrayOf<Long>(
                0xFFFFFFFF,
                0x808080FF,
                0x000000FF,
                0xFF0000FF,
                0xBFBF00FF,
                0x008000FF,
                0x80FFFFFF,
                0x8080FFFF,
                0xBF40BFFF,
                0xA0A424FF,
                0x411BEAFF,
                0x1EAC41FF,
                0xF0C80EFF,
                0xB430E5FF,
                0xED7651FF,
                0xFEF888FF,
                0x19CB97FF,
                0x362698FF,
                0x7E7EB8FF
        )

        val rgb = hexadecimals.map { hex(it) }
        val hsl = rgb.map { hsl(it.hue, it.saturation, it.lightness, it.a) }

        for (i in rgb.indices) {
            assertEqualColor(rgb[i], hsl[i])
        }
    }

    private fun assertEqualColor(initial: Color, expected: Color) {
        assert(initial.r.isAlmostEqual(expected.r)) { printError(initial, expected, "red") }
        assert(initial.g.isAlmostEqual(expected.g)) { printError(initial, expected,"green") }
        assert(initial.b.isAlmostEqual(expected.b)) { printError(initial, expected, "blue") }
    }

    private fun printError(initial: Color, expected: Color, componentName: String) {
        System.err.println("Color: $initial was not converted properly for $componentName value")
        System.err.println("Initial Color:   ${initial.r}, ${initial.g}, ${initial.b}")
        System.err.println("Converted Color: ${expected.r}, ${expected.g}, ${expected.b}")
    }

    private fun Double.isAlmostEqual(other: Double): Boolean {
        val onePercent = 0.01
        return this in (other - onePercent)..(other + onePercent)
    }
}