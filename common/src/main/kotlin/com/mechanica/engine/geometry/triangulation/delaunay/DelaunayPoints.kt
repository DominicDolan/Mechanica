package com.mechanica.engine.geometry.triangulation.delaunay

import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.cross
import com.cave.library.vector.vec2.minus
import com.cave.library.vector.vec2.vec
import kotlin.math.sign


sealed class SuperPoint : Vector2 {
    override val x: Double
        get() = Double.NaN
    override val y: Double
        get() = Double.NaN

    object TopPoint : SuperPoint()
    object BottomPoint : SuperPoint()
}


fun Vector2.isLexicographicallyGreater(other: Vector2): Boolean {
    if (x > other.x) {
        return true
    } else if (x == other.x && y > other.y) {
        return true
    }
    return false
}

fun Vector2.isOnRightOf(edge: DelaunayEdge): Int {
    return isOnRightOf(edge.p1, edge.p2)
}

fun Vector2.isOnRightOf(p1: Vector2, p2: Vector2): Int {
    if (p1 !is SuperPoint && p2 !is SuperPoint && this !is SuperPoint) {
        val a = vec(this); val b = vec(p1); val c = vec(p2)

        return (a - b).cross(c - b).sign.toInt()
    } else if (this is SuperPoint) {
        return when(this) {
            SuperPoint.TopPoint -> if (p1.isLexicographicallyGreater(p2)) 1 else -1
            SuperPoint.BottomPoint -> if (p2.isLexicographicallyGreater(p1)) 1 else -1
        }
    } else {
        if (p1 is SuperPoint && p2 is SuperPoint) {
            return if (p1 is SuperPoint.BottomPoint) 1 else -1
        }

        if (p1 is SuperPoint) {
            return if(p2.isLexicographicallyGreater(this) != p1 is SuperPoint.BottomPoint) 1 else -1
        } else if (p2 is SuperPoint) {
            return if (this.isLexicographicallyGreater(p1) != p2 is SuperPoint.BottomPoint) 1 else -1
        }
        return -1
    }
}