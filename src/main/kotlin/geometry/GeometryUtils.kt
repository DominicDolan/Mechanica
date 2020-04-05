package geometry

import util.units.Vector
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