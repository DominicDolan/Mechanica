package com.mechanica.engine.drawer.subclasses.color

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.LightweightColor
import com.mechanica.engine.color.hsl
import com.mechanica.engine.color.hex
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.unit.angle.Angle

interface ColorDrawer : Drawer, Color {
    fun get(): Color

    operator fun invoke(color: Color): ColorDrawer

    operator fun invoke(color: LightweightColor): ColorDrawer

    operator fun invoke(hex: Long): ColorDrawer = invoke(hex(hex))

    fun alpha(alpha: Double): ColorDrawer {
        this.invoke(com.mechanica.engine.color.rgba(r, g, b, alpha))
        return this
    }

    fun rgba(r: Double = this.r, g: Double = this.g, b: Double = this.b, a: Double = this.a): ColorDrawer {
        this.invoke(com.mechanica.engine.color.rgba(r, g, b, a))
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

    fun strokeColor(color: LightweightColor, strokeWidth: Double = -1.0): ColorDrawer
    fun strokeColor(color: Color, strokeWidth: Double = -1.0): ColorDrawer

    fun fillColor(color: LightweightColor): ColorDrawer
    fun fillColor(color: Color): ColorDrawer
}
