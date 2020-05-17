package com.mechanica.engine.geometry

import com.mechanica.engine.geometry.lines.LineSegment
import com.mechanica.engine.unit.vector.Vector
import kotlin.math.abs
import kotlin.math.sign

fun Vector.isLeftOf(line: LineSegment) = rectangleArea(this, line.p1, line.p2) > 0.0

fun rectangleArea(p0: Vector, p1: Vector, p2: Vector): Double {
    return (((p2.x - p1.x)*(p0.y - p1.y)) - ((p2.y - p1.y)*(p0.x - p1.x)))
}

fun Vector.isInTriangle(p0: Vector, p1: Vector, p2: Vector): Boolean {
    val area = rectangleArea(p0, p1, p2)
    val sign = sign(area)
    val s = (p0.y * p2.x - p0.x * p2.y + (p2.y - p0.y) * this.x + (p0.x - p2.x) * this.y) * sign
    val t = (p0.x * p1.y - p0.y * p1.x + (p0.y - p1.y) * this.x + (p1.x - p0.x) * this.y) * sign

    return s > 0.0 && t > 0.0 && (s + t) < abs(area)
}

fun Array<Vector>.containsPoint(point: Vector) = arrayContainsPoint(this, point)

fun Vector.isInPath(path: Array<Vector>) = arrayContainsPoint(path, this)

private fun arrayContainsPoint(array: Array<Vector>, point: Vector): Boolean {
    val len = array.size
    var result = false

    for (i in 0 until len){
        val j = (i + len - 1)%len
        if ((array[i].y > point.y) != (array[j].y > point.y) &&
                (point.x < (array[j].x - array[i].x) * (point.y - array[i].y) / (array[j].y-array[i].y) + array[i].x)) {
            result = !result
        }
    }
    return result
}