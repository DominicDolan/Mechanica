package com.mechanica.engine.drawer.subclasses.layout

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector

interface LayoutDrawer {
    fun origin(): Drawer
    fun origin(x: Number, y: Number): Drawer
    fun origin(point: Vector) = origin(point.x, point.y)
    fun origin(point: LightweightVector) = origin(point.x, point.y)
}