package com.mechanica.engine.drawer.subclasses.layout

import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.drawer.Drawer

interface OriginDrawer {
    fun normalized(x: Number, y: Number): Drawer
    fun normalized(point: Vector2) = normalized(point.x, point.y)
    fun normalized(point: InlineVector) = normalized(point.x, point.y)

    fun relative(x: Number, y: Number): Drawer
    fun relative(vector: InlineVector) = relative(vector.x, vector.y)
    fun relative(vector: Vector2) = relative(vector.x, vector.y)
}