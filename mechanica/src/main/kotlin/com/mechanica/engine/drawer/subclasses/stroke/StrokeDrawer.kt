package com.mechanica.engine.drawer.subclasses.stroke

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.InlineColor
import com.mechanica.engine.drawer.Drawer

interface StrokeDrawer : Drawer {
    operator fun invoke(stroke: Double): Drawer

    fun strokeColor(color: Color)
    fun strokeColor(color: InlineColor)
}