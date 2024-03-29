package com.mechanica.engine.drawer.subclasses.color

import com.cave.library.angle.Angle
import com.cave.library.color.Color
import com.cave.library.color.InlineColor
import com.cave.library.color.hex
import com.cave.library.color.hsl
import com.mechanica.engine.drawer.Drawer

interface ColorDrawer : Drawer, Color {
    fun get(): Color

    operator fun invoke(color: Color): ColorDrawer

    operator fun invoke(color: InlineColor): ColorDrawer

    operator fun invoke(hex: Long): ColorDrawer = invoke(hex(hex))

    fun alpha(alpha: Double): ColorDrawer {
        this.invoke(com.cave.library.color.rgba(r, g, b, alpha))
        return this
    }

    fun rgba(r: Double = this.r, g: Double = this.g, b: Double = this.b, a: Double = this.a): ColorDrawer {
        this.invoke(com.cave.library.color.rgba(r, g, b, a))
        return this
    }

    fun hue(hue: Angle): ColorDrawer {
        this.invoke(hsl(hue, saturation, lightness))
        return this
    }

    fun lightness(lightness: Double): ColorDrawer {
        this.invoke(hsl(hue, saturation, lightness))
        return this
    }

    fun saturation(saturation: Double): ColorDrawer {
        this.invoke(hsl(hue, saturation, lightness))
        return this
    }

    fun strokeColor(color: InlineColor, strokeWidth: Double = -1.0): ColorDrawer
    fun strokeColor(color: Color, strokeWidth: Double = -1.0): ColorDrawer

    fun fillColor(color: InlineColor): ColorDrawer
    fun fillColor(color: Color): ColorDrawer
}
