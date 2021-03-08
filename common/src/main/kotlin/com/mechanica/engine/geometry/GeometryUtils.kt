package com.mechanica.engine.geometry

import com.cave.library.vector.vec2.*
import com.mechanica.engine.geometry.lines.LineSegment
import kotlin.math.abs
import kotlin.math.sign

fun Vector2.isLeftOf(line: LineSegment) = rectangleArea(this, line.p1, line.p2) > 0.0

fun VectorTest() {
    
}
fun TestVector() {
    
}
fun rectangleArea(p0: Vector2, p1: Vector2, p2: Vector2): Double {
    return (((p2.x - p1.x)*(p0.y - p1.y)) - ((p2.y - p1.y)*(p0.x - p1.x)))
}

fun Vector2.isInTriangle(p0: Vector2, p1: Vector2, p2: Vector2): Boolean {
    val area = rectangleArea(p0, p1, p2)
    val sign = sign(area)
    val s = (p0.y * p2.x - p0.x * p2.y + (p2.y - p0.y) * this.x + (p0.x - p2.x) * this.y) * sign
    val t = (p0.x * p1.y - p0.y * p1.x + (p0.y - p1.y) * this.x + (p1.x - p0.x) * this.y) * sign

    return s > 0.0 && t > 0.0 && (s + t) < abs(area)
}

fun Array<Vector2>.containsPoint(point: Vector2) = arrayContainsPoint(this, point)

fun Vector2.isInPath(path: Array<Vector2>) = arrayContainsPoint(path, this)

private fun arrayContainsPoint(array: Array<Vector2>, point: Vector2): Boolean {
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

fun perpendicularDistance(vector: Vector2, line: LineSegment): Double {
    return perpendicularDistance(vector, line.p1, line.p2)
}

fun perpendicularDistance(vector: Vector2, lineP1: Vector2, lineP2: Vector2): Double {
    val p = vec(vector)
    val p1 = vec(lineP1)
    val p2 = vec(lineP2)
    val a = p - p1
    val b = p2 - p1
    val c = b.perpendicular()

    val dot = c.dot(a)

    return dot/c.r
}