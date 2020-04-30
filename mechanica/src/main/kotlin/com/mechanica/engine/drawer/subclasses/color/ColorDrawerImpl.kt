package com.mechanica.engine.drawer.subclasses.color

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.LightweightColor
import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.drawer.Drawer

internal class ColorDrawerImpl(drawer: Drawer, private val data: DrawData): ColorDrawer, Color by data.fillColor, Drawer by drawer {

    override fun get() = data.fillColor

    override fun invoke(color: Color): Drawer {
        data.fillColor.set(color)
        return this
    }

    override fun invoke(color: LightweightColor): Drawer {
        data.fillColor.set(color)
        return this
    }

    override fun fillColor(color: Color): ColorDrawer {
        invoke(color)
        return this
    }

    override fun fillColor(color: LightweightColor): ColorDrawer {
        invoke(color)
        return this
    }

    override fun strokeColor(color: Color, strokeWidth: Double): ColorDrawer {
        if (strokeWidth >= 0.0) {
            data.strokeWidth = strokeWidth
        }
        data.strokeColor.set(color)
        return this
    }

    override fun strokeColor(color: LightweightColor, strokeWidth: Double): ColorDrawer {
        if (strokeWidth >= 0.0) {
            data.strokeWidth = strokeWidth
        }
        data.strokeColor.set(color)
        return this
    }
}