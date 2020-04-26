package drawer.subclasses.color

import drawer.Drawer
import util.colors.Color
import util.colors.HexColor
import util.colors.hex
import util.colors.hsl
import util.units.Angle

interface ColorDrawer2 : Drawer, Color {
    fun get(): Color

    operator fun invoke(color: Color): Drawer

    operator fun invoke(color: HexColor): Drawer

    operator fun invoke(hex: Long): Drawer = invoke(hex(hex))

    fun alpha(alpha: Double): Drawer {
        this.invoke(util.colors.rgba(r, g, b, alpha))
        return this
    }

    fun rgba(r: Double = this.r, g: Double = this.g, b: Double = this.b, a: Double = this.a): Drawer {
        this.invoke(util.colors.rgba(r, g, b, a))
        return this
    }

    fun hue(hue: Angle): Drawer {
        this.invoke(hsl(hue, saturation, lightness))
        return this
    }

    fun lightness(lightness: Double): Drawer {
        this.invoke(hsl(hue, saturation, lightness))
        return this
    }

    fun saturation(saturation: Double): Drawer {
        this.invoke(hsl(hue, saturation, lightness))
        return this
    }

    fun strokeColor(color: HexColor, strokeWidth: Double = -1.0): ColorDrawer2
    fun strokeColor(color: Color, strokeWidth: Double = -1.0): ColorDrawer2

    fun fillColor(color: HexColor): ColorDrawer2
    fun fillColor(color: Color): ColorDrawer2
}
