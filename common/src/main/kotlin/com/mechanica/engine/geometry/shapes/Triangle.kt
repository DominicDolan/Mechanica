package com.mechanica.engine.geometry.shapes

import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.cross
import com.cave.library.vector.vec2.minus
import com.cave.library.vector.vec2.vec
import kotlin.math.sign


interface Triangle {
    val a: Vector2
    val b: Vector2
    val c: Vector2

    fun contains(point: Vector2): Int {
        val a = vec(a)
        val b = vec(b)
        val c = vec(c)

        val p = vec(point)

        val pab = (p - a).cross(b - a).sign
        val pbc = (p - b).cross(c - b).sign

        if (pab == 0.0 || pbc == 0.0) return 0

        if (pab != pbc) return -1

        val pca = (p - c).cross(a - c).sign
        if (pca == 0.0) return 0

        return if (pab == pca) return 1 else -1
    }

    operator fun component1() = a
    operator fun component2() = b
    operator fun component3() = c

    operator fun get(i: Int): Vector2 {
        return when(i) {
            0 -> a
            1 -> b
            2 -> c
            else -> throw ArrayIndexOutOfBoundsException("Tried to get index 3 of a triangle")
        }
    }


    fun hasPoints(p1: Vector2, p2: Vector2) = hasPoint(p1) && hasPoint(p2)

    fun hasPoint(p: Vector2) = a == p || b == p || c == p

    companion object {
        fun create(a: Vector2, b: Vector2, c: Vector2): Triangle {
            return object : Triangle {
                override val a: Vector2 = Vector2.create(a)
                override val b: Vector2 = Vector2.create(b)
                override val c: Vector2 = Vector2.create(c)
            }
        }

        fun create() = create(vec(0.0, 0.0), vec(0.5, 1.0), vec(1.0, 0.0))
    }
}


