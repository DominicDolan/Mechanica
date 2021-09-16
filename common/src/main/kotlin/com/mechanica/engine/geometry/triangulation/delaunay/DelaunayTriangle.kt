package com.mechanica.engine.geometry.triangulation.delaunay

import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.minus
import com.cave.library.vector.vec2.rSquared
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.geometry.lines.LineSegment
import com.mechanica.engine.geometry.shapes.Triangle


class DelaunayTriangle(
    override val a: Vector2,
    override val b: Vector2,
    override val c: Vector2
) : Triangle {
    val ab = object : DelaunayEdge(this@DelaunayTriangle) {
        override val p1: Vector2
            get() = this@DelaunayTriangle.a
        override val p2: Vector2
            get() = this@DelaunayTriangle.b
        override val opposite: Vector2
            get() = this@DelaunayTriangle.c
    }

    val bc = object : DelaunayEdge(this@DelaunayTriangle) {
        override val p1: Vector2
            get() = this@DelaunayTriangle.b
        override val p2: Vector2
            get() = this@DelaunayTriangle.c
        override val opposite: Vector2
            get() = this@DelaunayTriangle.a
    }

    val ca = object : DelaunayEdge(this@DelaunayTriangle) {
        override val p1: Vector2
            get() = this@DelaunayTriangle.c
        override val p2: Vector2
            get() = this@DelaunayTriangle.a
        override val opposite: Vector2
            get() = this@DelaunayTriangle.b
    }

    init {
        require(a != b && b != c && c != a) {
            "Triangle should have 3 distinct points"
        }
    }

    override fun contains(point: Vector2): Boolean {
        val ab = point.isOnRightOf(a, b)
        val bc = point.isOnRightOf(b, c)
        val ca = point.isOnRightOf(c, a)
        return (ab == bc) && (bc == ca)
    }

    val hasSuperPoint: Boolean
        get() = anyPoint { it is SuperPoint }

    fun getEdge(i: Int): DelaunayEdge {
        return when(i) {
            0 -> ab
            1 -> bc
            2 -> ca
            else -> throw ArrayIndexOutOfBoundsException("Tried to get index 3 of a triangle")
        }
    }

    fun circumcircleContains(point: Vector2): Boolean {
        fun isClockwise(): Boolean {
            val ac = vec(a) - vec(c)
            val bc = vec(b) - vec(c)

            val det = ac.x * bc.y - ac.y * bc.x;

            return det < 0.0;
        }

        val ap = vec(a) - vec(point)
        val bp = vec(b) - vec(point)
        val cp = vec(c) - vec(point)

        val ar = ap.rSquared; val br = bp.rSquared; val cr = cp.rSquared

        val det = ap.x * bp.y * cr + ap.y * br * cp.x + ar * bp.x * cp.y - ar * bp.y * cp.x - ap.y * bp.x * cr - ap.x * br * cp.y

        return if (isClockwise()) {
            det < 0.0
        } else det > 0.0

    }

    inline fun findOnePoint(condition: (Vector2) -> Boolean): Vector2 {
        var selected: Vector2? = null

        for(i in 0..2) {
            val point = get(i)
            if (condition(point)) {
                if (selected != null) throw IllegalStateException("More than one point satisfies the given condition")
                selected = point
            }
        }

        if (selected == null) throw IllegalStateException("No points satisfy the given condition")

        return selected
    }

    inline fun anyPoint(condition: (Vector2) -> Boolean): Boolean {
        var any = false
        for(i in 0..2) {
            val point = get(i)
            if (condition(point)) {
                any = true
            }
        }
        return any
    }

    inline fun findOneEdge(condition: (DelaunayEdge) -> Boolean): DelaunayEdge {
        var selected: DelaunayEdge? = null

        for(i in 0..2) {
            val point = getEdge(i)
            if (condition(point)) {
                if (selected != null) {
                    throw IllegalStateException("More than one edge satisfies the given condition")
                }
                selected = point
            }
        }

        if (selected == null) throw IllegalStateException("No edges satisfy the given condition")

        return selected
    }

    inline fun anyEdge(condition: (DelaunayEdge) -> Boolean): Boolean {
        var any = false
        for(i in 0..2) {
            val edge = getEdge(i)
            if (condition(edge)) {
                any = true
            }
        }
        return any
    }

    inline fun forEachEdge(action: (DelaunayEdge) -> Unit) {
        for(i in 0..2) {
            val edge = getEdge(i)
            action(edge)
        }
    }

    inline fun forEachPoint(action: (Vector2) -> Unit) {
        for(i in 0..2) {
            val point = get(i)
            action(point)
        }
    }

}

abstract class DelaunayEdge(val triangle: DelaunayTriangle) : LineSegment() {
    abstract val opposite: Vector2

    val hasSuperPoint: Boolean
        get() = p1 is SuperPoint || p2 is SuperPoint

    override fun equals(other: Any?): Boolean {
        return if (other != null && other is DelaunayEdge) {
            hasBothPoints(other.p1, other.p2)
        } else false
    }

    fun hasBothPoints(p1: Vector2, p2: Vector2): Boolean {
        return (p1 == this.p1 && p2 == this.p2) || (p1 == this.p2 && p2 == this.p1)
    }

    override fun hashCode(): Int {
        var result = triangle.hashCode()
        result = 31 * result + opposite.hashCode()
        return result
    }
}