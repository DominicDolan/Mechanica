package geometry

import util.units.Vector

fun Vector.isLeftOf(line: LineSegment) = this.closeness(line) > 0.0

fun Vector.closeness(line: LineSegment): Double {
    return (((line.p2.x - line.p1.x)*(this.y - line.p1.y)) - ((line.p2.y - line.p1.y)*(this.x - line.p1.x)))
}