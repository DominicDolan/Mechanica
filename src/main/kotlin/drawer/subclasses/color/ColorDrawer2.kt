package drawer.subclasses.color

import drawer.Drawer2
import util.colors.Color
import util.colors.HexColor
import util.colors.hex
import util.colors.hsl
import util.units.Angle

interface ColorDrawer2 : Drawer2, Color {
    fun get(): Color

    operator fun invoke(color: Color): Drawer2

    operator fun invoke(color: HexColor): Drawer2

    operator fun invoke(hex: Long): Drawer2 = invoke(hex(hex))

    fun alpha(alpha: Double): Drawer2 {
        this.invoke(util.colors.rgba(r, g, b, alpha))
        return this
    }

    fun rgba(r: Double = this.r, g: Double = this.g, b: Double = this.b, a: Double = this.a): Drawer2 {
        this.invoke(util.colors.rgba(r, g, b, a))
        return this
    }

    fun hue(hue: Angle): Drawer2 {
        this.invoke(hsl(hue, saturation, lightness))
        return this
    }

    fun lightness(lightness: Double): Drawer2 {
        this.invoke(hsl(hue, saturation, lightness))
        return this
    }

    fun saturation(saturation: Double): Drawer2 {
        this.invoke(hsl(hue, saturation, lightness))
        return this
    }

    fun strokeColor(color: HexColor, strokeWidth: Double = -1.0): ColorDrawer2
    fun strokeColor(color: Color, strokeWidth: Double = -1.0): ColorDrawer2

    fun fillColor(color: HexColor): ColorDrawer2
    fun fillColor(color: Color): ColorDrawer2
}
