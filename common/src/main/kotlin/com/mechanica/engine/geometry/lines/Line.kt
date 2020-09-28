package com.mechanica.engine.geometry.lines

import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.vec

interface Line {
    val m: Double
    val b: Double
    val n: Double
    val c: Double

    fun intersect(other: Line): InlineVector {
        val x = if (n != 0.0 && other.n !=0.0) (other.b - b) / (m - other.m)
        else if (n == 0.0) c
        else other.c

        val y = if (m != 0.0 && other.m != 0.0) (other.c - c) / (other.n - n)
        else if (m==0.0) b
        else other.b

        return vec(x, y)
    }
}