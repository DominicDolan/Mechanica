package com.mechanica.engine.geometry.lines

import com.mechanica.engine.unit.vector.Vector

class LineSegmentImpl(p1: Vector, p2: Vector) : LineSegment() {

    override val p1 = LinePoint(p1)
    override val p2 = LinePoint(p2)

    inner class LinePoint(vec: Vector) : Vector {
        override var x: Double = vec.x
            set(value) {
                field = value
                hasChanged = true
            }
        override var y: Double = vec.y
            set(value) {
                field = value
                hasChanged = true
            }

        override fun toString(): String {
            return "%.2f, %.2f".format(x, y)
        }
    }

}