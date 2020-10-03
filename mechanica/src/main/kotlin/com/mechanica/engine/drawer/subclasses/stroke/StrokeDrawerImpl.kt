package com.mechanica.engine.drawer.subclasses.stroke

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.InlineColor
import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.drawer.Drawer

class StrokeDrawerImpl(drawer: Drawer, private val data: DrawData) : StrokeDrawer, Drawer by drawer {
    override operator fun invoke(stroke: Double): Drawer {
        data.strokeWidth = stroke
        return this
    }

    override fun strokeColor(color: Color) {
        data.strokeColor.set(color)
    }

    override fun strokeColor(color: InlineColor) {
        data.strokeColor.set(color)
    }
}