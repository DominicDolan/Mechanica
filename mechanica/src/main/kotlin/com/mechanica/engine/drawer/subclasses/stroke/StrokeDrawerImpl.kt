package com.mechanica.engine.drawer.subclasses.stroke

import com.cave.library.color.Color
import com.cave.library.color.InlineColor
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.state.DrawState

class StrokeDrawerImpl(drawer: Drawer, private val state: DrawState) : StrokeDrawer, Drawer by drawer {
    override operator fun invoke(stroke: Double): Drawer {
        state.setStrokeWidth(stroke)
        return this
    }

    override fun strokeColor(color: Color) {
        state.setStrokeColor(color)
    }

    override fun strokeColor(color: InlineColor) {
        state.setStrokeColor(color)
    }
}