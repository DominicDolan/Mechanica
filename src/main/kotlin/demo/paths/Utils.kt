package demo.paths

import util.extensions.vec
import util.units.Vector


private val line1 = LineEquation(0.5, 0.0)
private val line2 = LineEquation(0.5, 0.0)

private fun bezierFromPrevious(prereference: Vector, previous: Vector, current: Vector, next: Vector): Vector {
    val m1 = (previous.y - prereference.y)/(previous.x - prereference.x)

    val line1 = line1.from(prereference, previous)
    val line2 = line2.from(current, next)

    val point = line1.intercept(line2)

    return point
//        val isVertical = m1.isInfinite() || m1.isNaN()
//        val isHorizontal = m1 == 0.0
//
//        val b1 =  current.y - m1*current.x
//
//        val m2 = -1/m1
//
//        val b2 = next.y - m2*next.x
//
//        val x = if (!isVertical && !isHorizontal) {
//            (b1 - b2)/(m2 - m1)
//        } else if (isVertical) {
//            current.x
//        } else {
//            next.x
//        }
//
//        val y = if (!isVertical && !isHorizontal) {
//            b1 + m1*x
//        } else if (isVertical) {
//            next.y
//        } else {
//            current.y
//        }

//        bezier(previous, point, current)
}

private class LineEquation(m: Double, b: Double) {
    var m = m
    var b = b
    private val xIntercept: Double
        get() = -b/m

    val isVertical: Boolean
        get() = (m.isInfinite() || m.isNaN())
    val isHorizontal: Boolean
        get() = m == 0.0
    val isOrtho: Boolean
        get() = isHorizontal || isVertical

    fun from(p1: Vector, p2: Vector): LineEquation {
        m = (p2.y - p1.y)/(p2.x - p1.x)

        b =  p2.y - m*p2.x

        return this
    }

    fun getY(x: Double, valueForVertical: Double = 0.0): Double {
        return if (!isVertical && !isHorizontal) {
            b + m*x
        } else if (isVertical) {
            valueForVertical
        } else {
            b
        }
    }
    fun getX(y: Double, valueForHorizontal: Double = 0.0): Double {
        return if (!isOrtho) {
            (y - b)/m
        } else if (isVertical) {
            valueForHorizontal
        } else {
            xIntercept
        }
    }

    override fun toString(): String {
        return "y = ${m}x + $b"
    }
}

private fun LineEquation.intercept(other: LineEquation): Vector {
    var x = 0.0
    var y = 0.0
    if (!isOrtho && !other.isOrtho) {
        println("None Ortho")
        x = (b - other.b)/(other.m - m)
        y = getY(x)
    } else if (isOrtho && !other.isOrtho) {
        println("line 1 is ortho: $line1")
        if (isVertical) {
            x = getX(0.0)
            y = other.getY(x)
        } else if (isHorizontal) {
            y = getY(0.0)
            x = other.getX(y)
        }
    } else if (!isOrtho && other.isOrtho) {
        println("line 2 is ortho")
        if (other.isVertical) {
            x = other.getX(0.0)
            y = getY(x)
        } else if (other.isHorizontal) {
            y = other.getY(0.0)
            x = getX(y)
        }
    } else if (isOrtho && other.isOrtho) {
        println("both are ortho")
    }

    println("x: $x, y: $y")
    println()
    return vec(x, y)

}

