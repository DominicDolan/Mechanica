package com.mechanica.engine.geometry.triangulation

import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.geometry.isInTriangle
import com.mechanica.engine.geometry.rectangleArea
import com.mechanica.engine.geometry.triangulation.triangulators.GrahamScanTriangulator

fun calculateLineArea(p1: InlineVector, p2: InlineVector): Double {
    return (p2.x - p1.x)*(p2.y + p1.y)
}

fun isConcave(prev: Vector2, current: Vector2, next: Vector2, ccw: Boolean): Boolean {
    val area = rectangleArea(next, prev, current)
    val isLeft = area > 0.0
    return (isLeft && !ccw) || (!isLeft && ccw)
}

fun GrahamScanTriangulator.Node.isEar(concaveVertices: Iterable<GrahamScanTriangulator.Node>): Boolean {

    val p2 = this.next
    val p3 = this.prev
    for (n in concaveVertices) {
        if (n.isInTriangle(this, p2, p3)) return false
    }
    return (!this.isConcave)
}


