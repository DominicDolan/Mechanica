package com.mechanica.engine.geometry.lines

import com.cave.library.vector.vec2.Vector2

class LineSegmentImpl(p1: Vector2, p2: Vector2) : LineSegment() {

    override val p1 = LinePoint(p1)
    override val p2 = LinePoint(p2)

    inner class LinePoint(vec: Vector2) : Vector2 {
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