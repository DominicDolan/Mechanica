@file:Suppress("unused") // There will be many functions here that go unused most of the time
package com.mechanica.engine.unit.vector

import com.mechanica.engine.unit.angle.Angle
import com.mechanica.engine.unit.angle.radians
import kotlin.math.*


/**
 * Created by domin on 02/11/2017.
 */


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

operator fun LightweightVector.plus(other: LightweightVector) : LightweightVector {
    return vec(this.x.toFloat() + other.x.toFloat(), this.y.toFloat() + other.y.toFloat())
}

operator fun LightweightVector.minus(other: LightweightVector) : LightweightVector {
    return vec(this.x.toFloat() - other.x.toFloat(), this.y.toFloat() - other.y.toFloat())
}

operator fun LightweightVector.times(other: LightweightVector): Float = x.toFloat()* other.x.toFloat() + y.toFloat()* other.y.toFloat()

operator fun LightweightVector.times(scale: Number) = vec(this.x.toFloat() * scale.toFloat(), this.y.toFloat() * scale.toFloat())

operator fun LightweightVector.div(scale: Number) = vec(this.x.toFloat() / scale.toFloat(), this.y.toFloat() / scale.toFloat())

operator fun LightweightVector.unaryMinus() = vec(-this.x.toFloat(), -this.y.toFloat())

infix fun LightweightVector.equals(other: LightweightVector) = this.x.toFloat() == other.x.toFloat() && this.y.toFloat() == other.y.toFloat()


fun Vector.distanceTo(other: Vector): Float {
    return (vec(this) - vec(other)).r.toFloat()
}

fun LightweightVector.normalize() = vec(1.0, this.theta)

fun LightweightVector.dot(other: LightweightVector) = this.x*other.x + this.y*other.y

fun LightweightVector.cross(other: LightweightVector) = (this.x * other.y - this.y * other.x)
