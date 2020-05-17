package com.mechanica.engine.unit.vector

import com.mechanica.engine.unit.vector.Vector
import java.lang.Float.intBitsToFloat

inline class LightweightVector(private val xy: Long): Vector {
    override val x get() = intBitsToFloat((xy shr 32).toInt()).toDouble()
    override val y get() = intBitsToFloat(xy.toInt()).toDouble()

    fun toLong() = xy

    override fun toString(): String {
        return "($x, $y)"
    }
}