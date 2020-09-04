package com.mechanica.engine.drawer.superclass.circle

import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector

interface CircleDrawer {
    fun circle(x: Number = 0, y: Number = 0, radius: Number = -1.0f)

    fun circle(position: Vector, radius: Number = -1.0f) = circle(position.x, position.y, radius)
    fun circle(position: LightweightVector, radius: Number = -1.0f) = circle(position.x, position.y, radius)

    fun ellipse(x: Number = 0, y: Number = 0, width: Number = 1, height: Number = 1)
    fun ellipse(xy: LightweightVector, wh: LightweightVector) = ellipse(xy.x, xy.y, wh.x, wh.y)
}