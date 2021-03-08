package com.mechanica.engine.drawer.superclass.path

import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.geometry.lines.LineSegment
import com.mechanica.engine.util.VectorArray

interface PathDrawer {
    fun path(path: Array<out Vector2>, count: Int = path.size)
    fun path(path: VectorArray, count: Int = path.size)
    fun path(path: List<Vector2>, count: Int = path.size)

    fun line(x1: Number, y1: Number, x2: Number, y2: Number)
    fun line(p1: Vector2, p2: Vector2) = line(p1.x, p1.y, p2.x, p2.y)
    fun line(p1: InlineVector, p2: InlineVector) = line(p1.x, p1.y, p2.x, p2.y)
    fun line(line: LineSegment) = line(line.p1.x, line.p1.y, line.p2.x, line.p2.y)
}