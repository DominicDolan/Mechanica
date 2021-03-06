package com.mechanica.engine.geometry.lines

import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.Vector2
import kotlin.math.max
import kotlin.math.min

abstract class LineSegment() : Line {
    protected var hasChanged = true

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

    abstract val p1: Vector2
    abstract val p2: Vector2

    fun isInBoundingBox(point: InlineVector): Boolean {
        val xBoolean = point.x in min(p1.x, p2.x)..max(p1.x, p2.x)
        val yBoolean = point.y in min(p1.y,p2.y)..max(p2.y, p1.y)
        return xBoolean && yBoolean
    }

    fun segmentIntersect(other: LineSegment): Boolean {
        val intersect = intersect(other)
        val isInThis = isInBoundingBox(intersect)
        val isInOther = other.isInBoundingBox(intersect)
        return isInThis && isInOther
    }

    fun perpendicularDistance(vector: Vector2): Double {
        return com.mechanica.engine.geometry.perpendicularDistance(vector, this)
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

    override fun toString(): String {
        return "p1: $p1, p2: $p2, slope: %.2f, intercept Y: %.2f, intercept X: %.2f".format(m, b, c)
    }

    companion object {
        operator fun invoke(p1: Vector2, p2: Vector2): LineSegment {
            return LineSegmentImpl(p1, p2)
        }

        operator fun invoke(line: LineSegment): LineSegment {
            return LineSegmentImpl(line.p1, line.p2)
        }
    }
}