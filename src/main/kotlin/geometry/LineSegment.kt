package geometry

import debug.DebugDrawer
import util.units.LightweightVector
import util.units.Vector
import kotlin.math.max
import kotlin.math.min

class LineSegment(p1: Vector, p2: Vector) : Line {
    private var hasChanged = true

    private var _m: Double = 0.0
    override val m: Double
        get() = if (!hasChanged) _m else calculateSlope()
    private var _n: Double = 0.0
    override val n: Double
        get() = if (!hasChanged) _n else { -1.0/calculateSlope()}

    private var _b: Double = 0.0
    override val b: Double
        get() = if (!hasChanged) _b else calculateYIntercept()
    private var _c: Double = 0.0
    override val c: Double
        get() = if (!hasChanged) _c else calculateXIntercept()

    val p1 = LinePoint(p1)
    val p2 = LinePoint(p2)

    fun isInBoundingBox(point: LightweightVector): Boolean {
        val xBoolean = point.x in min(p1.x, p2.x)..max(p1.x, p2.x)
        val yBoolean = point.y in min(p1.y,p2.y)..max(p2.y, p1.y)
        return xBoolean && yBoolean
    }

    fun segmentIntersect(other: LineSegment): Boolean {
        val intersect = intersect(other)
        DebugDrawer.drawText("Segment Intersect: $intersect")
        return isInBoundingBox(intersect) && other.isInBoundingBox(intersect)
    }

    private fun calculateAll() {
        hasChanged = false
        _m = (p2.y - p1.y)/(p2.x - p1.x)
        _n = -1.0/_m
        _b = p1.y - _m*p1.x
        _c = p1.x + _n*p1.y
    }

    private fun calculateSlope(): Double {
        calculateAll()
        return _m
    }

    private fun calculateYIntercept(): Double {
        calculateAll()
        return _b
    }

    private fun calculateXIntercept(): Double {
        calculateAll()
        return _c
    }

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

    override fun toString(): String {
        return "p1: $p1, p2: $p2, slope: %.2f, intercept Y: %.2f, intercept X: %.2f".format(m, b, c)
    }
}