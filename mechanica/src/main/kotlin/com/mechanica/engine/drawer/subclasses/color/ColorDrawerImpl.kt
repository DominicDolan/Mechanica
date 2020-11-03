package com.mechanica.engine.drawer.subclasses.color

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.InlineColor
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.state.DrawState

internal class ColorDrawerImpl(drawer: Drawer, private val state: DrawState): ColorDrawer, Color by state.fillColor, Drawer by drawer {

    override fun get() = state.fillColor

    override fun invoke(color: Color): ColorDrawer {
        state.fillColor.set(color)
        return this
    }

    override fun invoke(color: InlineColor): ColorDrawer {
        state.fillColor.set(color)
        return this
    }

    override fun fillColor(color: Color): ColorDrawer {
        invoke(color)
        return this
    }

    override fun fillColor(color: InlineColor): ColorDrawer {
        invoke(color)
        return this
    }

    override fun strokeColor(color: Color, strokeWidth: Double): ColorDrawer {
        if (strokeWidth >= 0.0) {
            state.strokeWidth = strokeWidth
        }
        state.strokeColor.set(color)
        return this
    }

    override fun strokeColor(color: InlineColor, strokeWidth: Double): ColorDrawer {
        if (strokeWidth >= 0.0) {
            state.strokeWidth = strokeWidth
        }
        state.strokeColor.set(color)
        return this
    }
}