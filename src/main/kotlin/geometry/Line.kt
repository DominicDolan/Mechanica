package geometry

import debug.DebugDrawer
import util.extensions.vec
import util.units.LightweightVector

interface Line {
    val m: Double
    val b: Double
    val n: Double
    val c: Double

    fun intersect(other: Line): LightweightVector {
        val x = if (n != 0.0 && other.n !=0.0) (other.b - b) / (m - other.m)
        else if (n == 0.0) c
        else other.c

        val y = if (m != 0.0 && other.m != 0.0) (other.c - c) / (other.n - n)
        else if (m==0.0) b
        else other.b

        DebugDrawer.drawText("m1: %.2f, b1: %.2f, n1: %.2f, c1: %.2f".format(m, b, n, c))
        with(other) {
            DebugDrawer.drawText("m2: %.2f, b2: %.2f, n2: %.2f, c2: %.2f".format(m, b, n, c))
        }
        DebugDrawer.drawText("Intersect: x: %.2f, y: %.2f".format(x, y))
        return vec(x, y)
    }
}