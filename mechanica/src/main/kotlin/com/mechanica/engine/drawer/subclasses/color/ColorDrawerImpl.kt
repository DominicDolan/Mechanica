package com.mechanica.engine.drawer.subclasses.color

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.InlineColor
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.state.DrawState

internal class ColorDrawerImpl(drawer: Drawer, private val state: DrawState): ColorDrawer, Color by state.color.fill, Drawer by drawer {

    override fun get() = state.color.fill

    override fun invoke(color: Color): ColorDrawer {
        state.setFillColor(color)
        return this
    }

    override fun invoke(color: InlineColor): ColorDrawer {
        state.setFillColor(color)
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
            state.setStrokeWidth(strokeWidth)
        }
        state.setStrokeColor(color)
        return this
    }

    override fun strokeColor(color: InlineColor, strokeWidth: Double): ColorDrawer {
        if (strokeWidth >= 0.0) {
            state.setStrokeWidth(strokeWidth)
        }
        state.setStrokeColor(color)
        return this
    }
}