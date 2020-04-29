@file:Suppress("unused") // There will be many functions here that go unused most of the time
package com.mechanica.engine.unit.vector

import com.mechanica.engine.unit.angle.Angle
import com.mechanica.engine.unit.angle.radians
import kotlin.math.*


/**
 * Created by domin on 02/11/2017.
 */

fun List<Vector>.centroid(): Vector {
    var xAverage = 0.0
    var yAverage = 0.0
    this.forEachIndexed { i, vec ->
        xAverage = (xAverage*i + vec.x)/(i+1)
        yAverage = (yAverage*i + vec.y)/(i+1)
    }

    return vec(xAverage, yAverage)
}

fun Vector.distanceTo(other: Vector): Float {
    return (this - other).r.toFloat()
}

fun List<Vector>.toFloatArray(): FloatArray{
    val floatArray = FloatArray(this.size*3) {0f}
    this.forEachIndexed { i, v ->
        floatArray[3 * i] = v.x.toFloat()
        floatArray[3 * i + 1] = v.y.toFloat()
    }
    return floatArray
}

fun List<Vector>.normalizeX(width: Double): List<Vector> {
    val currentHeight = this.xRange()
    val factor = width/currentHeight
    this.scale(factor)
    return this
}


fun List<Vector>.normalizeY(height: Double): List<Vector> {
    val currentHeight = this.yRange()
    val factor = height/currentHeight
    this.scale(factor)
    return this
}

fun List<Vector>.flipVertically(): List<Vector> {
    return this.map {
        vec(it.x, -it.y)
    }
}

fun List<Vector>.toOrigin(): List<Vector> {
    val minX = (this.minBy { it.x } ?: vec(0.0, 0.0)).x
    val minY = (this.minBy { it.y } ?: vec(0.0, 0.0)).y
    return this.map {
        vec(it.x - minX, it.y - minY)
    }
}

fun List<Vector>.xRange(): Double {
    val max = this.maxBy { it.x } ?: vec(0.0, 0.0)
    val min = this.minBy { it.x } ?: vec(0.0, 0.0)
    return max.x - min.x
}

fun List<Vector>.yRange(): Double {
    val max = this.maxBy { it.y } ?: vec(0.0, 0.0)
    val min = this.minBy { it.y } ?: vec(0.0, 0.0)
    return max.y - min.y
}

fun List<Vector>.scale(factor: Double): List<Vector> {
    return this.map {
        vec(it.x * factor, it.y * factor)
    }
}


fun List<Vector>.scaleX(factor: Double): List<Vector> {
    return this.map {
        vec(it.x * factor, it.y)
    }
}


fun List<Vector>.scaleY(factor: Double): List<Vector> {
    return this.map {
        vec(it.x, it.y * factor)
    }
}

fun List<Vector>.calculateLength(): Double {
    var previous = this[0]
    var length = 0.0
    for (vector in this) {
        val x = vector.x - previous.x
        val y = vector.y - previous.y
        length += sqrt(x*x + y*y)
        previous = vector
    }
    return length
}

fun vec(x: Number, y: Number): LightweightVector {
    val xBits = java.lang.Float.floatToRawIntBits(x.toFloat())
    val yBits = java.lang.Float.floatToRawIntBits(y.toFloat())
    val xyBits = (yBits.toLong() shl 32 ushr 32) or (xBits.toLong() shl 32)
    return LightweightVector(xyBits)
}

fun vec(r: Number, theta: Angle): LightweightVector {
    val x = r.toDouble()*cos(theta.toRadians().asDouble())
    val y = r.toDouble()*sin(theta.toRadians().asDouble())
    return vec(x, y)
}

fun vec(vector: Vector) = vec(vector.x, vector.y)

operator fun Vector.component1() = this.x
operator fun Vector.component2() = this.y

val Vector.r get() = hypot(x, y)

val Vector.theta get() = atan2(this.y, this.x).radians

operator fun Vector.plus(other: Vector) : Vector {
    return vec(this.x.toFloat() + other.x.toFloat(), this.y.toFloat() + other.y.toFloat())
}

operator fun Vector.minus(other: Vector) : Vector {
    return vec(this.x.toFloat() - other.x.toFloat(), this.y.toFloat() - other.y.toFloat())
}

operator fun Vector.times(other: Vector): Float = x.toFloat()* other.x.toFloat() + y.toFloat()* other.y.toFloat()

operator fun Vector.times(scale: Number) = vec(this.x.toFloat() * scale.toFloat(), this.y.toFloat() * scale.toFloat())

operator fun Vector.div(scale: Number) = vec(this.x.toFloat() / scale.toFloat(), this.y.toFloat() / scale.toFloat())

operator fun Vector.unaryMinus() = vec(-this.x.toFloat(), -this.y.toFloat())

infix fun Vector.equals(other: Vector) = this.x.toFloat() == other.x.toFloat() && this.y.toFloat() == other.y.toFloat()

fun Vector.normalize() = vec(1.0, this.theta)

fun Vector.dot(other: Vector) = this.x*other.x + this.y*other.y

fun Vector.cross(other: Vector) = (this.x * other.y - this.y * other.x)