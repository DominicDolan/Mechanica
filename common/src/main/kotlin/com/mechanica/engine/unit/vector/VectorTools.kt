@file:Suppress("unused") // There will be many functions here that go unused most of the time
package com.mechanica.engine.unit.vector

import com.mechanica.engine.unit.angle.Degree
import com.mechanica.engine.unit.angle.Radian
import kotlin.math.cos
import kotlin.math.sin


/**
 * Created by domin on 02/11/2017.
 */


fun vec(x: Number, y: Number): InlineVector {
    val xBits = java.lang.Float.floatToRawIntBits(x.toFloat())
    val yBits = java.lang.Float.floatToRawIntBits(y.toFloat())
    val xyBits = (yBits.toLong() shl 32 ushr 32) or (xBits.toLong() shl 32)
    return InlineVector(xyBits)
}

fun vec(r: Number, theta: Degree): InlineVector {
    val x = r.toDouble()*cos(theta.toRadians().toDouble())
    val y = r.toDouble()*sin(theta.toRadians().toDouble())
    return vec(x, y)
}

fun vec(r: Number, theta: Radian): InlineVector {
    val x = r.toDouble()*cos(theta.toDouble())
    val y = r.toDouble()*sin(theta.toDouble())
    return vec(x, y)
}

fun vec(vector: Vector) = vec(vector.x, vector.y)

operator fun Vector.component1() = this.x
operator fun Vector.component2() = this.y

operator fun InlineVector.plus(other: InlineVector) : InlineVector {
    return vec(this.x.toFloat() + other.x.toFloat(), this.y.toFloat() + other.y.toFloat())
}

operator fun InlineVector.minus(other: InlineVector) : InlineVector {
    return vec(this.x.toFloat() - other.x.toFloat(), this.y.toFloat() - other.y.toFloat())
}

operator fun InlineVector.times(other: InlineVector): Float = x.toFloat()* other.x.toFloat() + y.toFloat()* other.y.toFloat()

operator fun InlineVector.times(scale: Number) = vec(this.x.toFloat() * scale.toFloat(), this.y.toFloat() * scale.toFloat())

operator fun InlineVector.div(scale: Number) = vec(this.x.toFloat() / scale.toFloat(), this.y.toFloat() / scale.toFloat())

operator fun InlineVector.unaryMinus() = vec(-this.x.toFloat(), -this.y.toFloat())

infix fun InlineVector.equals(other: InlineVector) = this.x.toFloat() == other.x.toFloat() && this.y.toFloat() == other.y.toFloat()

operator fun DynamicVector.plusAssign(vector: Vector) {
    this.plusAssign(vec(vector))
}

operator fun DynamicVector.minusAssign(vector: Vector) {
    this.minusAssign(vec(vector))
}

operator fun DynamicVector.plusAssign(vector: InlineVector) {
    this.set(vec(this) + vector)
}

operator fun DynamicVector.minusAssign(vector: InlineVector) {
    this.set(vec(this) - vector)
}

fun Vector.distanceTo(other: Vector): Float {
    return (vec(this) - vec(other)).r.toFloat()
}

fun InlineVector.normalize() = vec(1.0, this.theta)

fun InlineVector.dot(other: InlineVector) = this.x*other.x + this.y*other.y

fun InlineVector.cross(other: InlineVector) = (this.x * other.y - this.y * other.x)
