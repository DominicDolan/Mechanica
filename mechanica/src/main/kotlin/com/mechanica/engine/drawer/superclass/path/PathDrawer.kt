package com.mechanica.engine.drawer.superclass.path

import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.VectorArray

interface PathDrawer {
    fun path(path: Array<out Vector>, count: Int = path.size)
    fun path(path: VectorArray, count: Int = path.size)
    fun path(path: List<Vector>, count: Int = path.size)

    fun line(x1: Number, y1: Number, x2: Number, y2: Number)
    fun line(p1: Vector, p2: Vector) = line(p1.x, p1.y, p2.x, p2.y)
    fun line(p1: LightweightVector, p2: LightweightVector) = line(p1.x, p1.y, p2.x, p2.y)
}