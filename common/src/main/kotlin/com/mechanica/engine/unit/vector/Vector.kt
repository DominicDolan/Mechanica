package com.mechanica.engine.unit.vector

import com.mechanica.engine.unit.angle.radians
import kotlin.math.atan2
import kotlin.math.hypot

interface Vector {
    val x: Double
    val y: Double

    val r get() = hypot(x, y)

    val theta get() = atan2(this.y, this.x).radians

    operator fun get(i: Int): Double {
        return if (i == 0) x else if (i == 1) y else throw ArrayIndexOutOfBoundsException("Tried to call 3rd coordinate on a 2D vector")
    }

    fun getOrZero(i: Int): Double {
        return if (i == 0) x else if (i == 1) y else 0.0
    }

    companion object {
        fun create(x: Double = 0.0, y: Double = 0.0) = object : Vector {
            override val x = x
            override val y = y
            override fun toString() = Companion.toString(this)
        }

        fun create(vector: InlineVector) = object : Vector {
            override val x = vector.x
            override val y = vector.y
            override fun toString() = Companion.toString(this)
        }

        fun create(vector: Vector) = object : Vector {
            override val x = vector.x
            override val y = vector.y
            override fun toString() = Companion.toString(this)
        }

        fun toString(vector: Vector) = "(${vector.x}, ${vector.y})"
    }
}