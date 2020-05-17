package com.mechanica.engine.geometry.triangulation

import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.geometry.isInTriangle
import com.mechanica.engine.geometry.rectangleArea

fun calculateLineArea(p1: LightweightVector, p2: LightweightVector): Double {
    return (p2.x - p1.x)*(p2.y + p1.y)
}

fun isConcave(prev: Vector, current: Vector, next: Vector, ccw: Boolean): Boolean {
    val area = rectangleArea(next, prev, current)
    val isLeft = area > 0.0
    return (isLeft && !ccw) || (!isLeft && ccw)
}

fun Triangulator.Node.isEar(concaveVertices: Iterable<Triangulator.Node>): Boolean {

    val p2 = this.next
    val p3 = this.prev
    for (n in concaveVertices) {
        if (n.isInTriangle(this, p2, p3)) return false
    }
    return (!this.isConcave)
}


