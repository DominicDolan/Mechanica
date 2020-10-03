package com.mechanica.engine.drawer.subclasses.layout

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.Vector

interface OriginDrawer {
    fun normalized(x: Number, y: Number): Drawer
    fun normalized(point: Vector) = normalized(point.x, point.y)
    fun normalized(point: InlineVector) = normalized(point.x, point.y)

    fun relative(x: Number, y: Number): Drawer
    fun relative(vector: InlineVector) = relative(vector.x, vector.y)
    fun relative(vector: Vector) = relative(vector.x, vector.y)
}