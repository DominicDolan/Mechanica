package com.mechanica.engine.drawer.subclasses.layout

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector

interface OriginDrawer {
    fun relative(): Drawer
    fun relative(x: Number, y: Number): Drawer
    fun relative(point: Vector) = relative(point.x, point.y)
    fun relative(point: LightweightVector) = relative(point.x, point.y)
}