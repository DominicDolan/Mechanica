package com.mechanica.engine.drawer.superclass.circle

import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.Vector2

interface CircleDrawer {
    fun circle(x: Number = 0, y: Number = 0, radius: Number = -1.0f)

    fun circle(position: Vector2, radius: Number = -1.0f) = circle(position.x, position.y, radius)
    fun circle(position: InlineVector, radius: Number = -1.0f) = circle(position.x, position.y, radius)

    fun ellipse(x: Number = 0, y: Number = 0, width: Number = 1, height: Number = 1)
    fun ellipse(xy: InlineVector, wh: InlineVector) = ellipse(xy.x, xy.y, wh.x, wh.y)
}