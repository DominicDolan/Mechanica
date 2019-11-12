@file:Suppress("unused") // There will be many functions here that go unused most of the time
package util.extensions

import compatibility.VectorConverter
import graphics.Polygon
import org.jbox2d.common.Vec2
import util.units.Angle
import util.units.LightweightVector
import util.units.Vector
import kotlin.math.*

/**
 * Created by domin on 02/11/2017.
 */

fun Vec2.hyp() = hypot(x.toDouble(),y.toDouble())

operator fun Vec2.plus(other: Vec2) : Vec2 {
    return Vec2(this.x + other.x, this.y + other.y)
}

operator fun Vec2.minus(other: Vec2) : Vec2 {
    return Vec2(this.x - other.x, this.y - other.y)
}

operator fun Vec2.plusAssign(other: Vec2) {
    this.x += other.x
    this.y += other.y
}

operator fun Vec2.minusAssign(other: Vec2) {
    this.x -= other.x
    this.y -= other.y
}

operator fun Vec2.times(other: Vec2): Float = x* other.x + y* other.y


operator fun Vec2.times(scale: Double): Vec2 = this.set(this.x*scale.toFloat(), this.y*scale.toFloat())

operator fun Vec2.timesAssign(scale: Double) {
    this.x *= scale.toFloat()
    this.y *= scale.toFloat()
}


operator fun Vec2.divAssign(scale: Float) {
    this.x /= scale
    this.y /= scale
}

operator fun Vec2.unaryMinus() : Vec2 {
    x = -x
    y = -y
    return this
}

fun Vec2.midpoint(other: Vec2) : Vec2{
    return Vec2((this.x + other.x)/2f, (this.y + other.y)/2f)
}

fun Vec2.angle(): Double{
    return atan2(this.y.toDouble(), this.x.toDouble())
}

fun Vec2.rotate(angle: Double, center: Vec2){
    this -= center
    val r = this.hyp()
    val a = this.angle() + angle

    val newX = (r* cos(a)).toFloat()
    val newY = (r* sin(a)).toFloat()

    this.set(center.x + newX, center.y + newY)
}

fun Vec2.scale(scale: Double, center: Vec2){
    this -= center
    this *= scale
    this.set(center.x + this.x, center.y + this.y)
}

fun Vec2.toVec() = vec(this.x, this.y)

fun List<Vector>.centroid(): Vector {
    var xAverage = 0.0
    var yAverage = 0.0
    this.forEachIndexed { i, vec ->
        xAverage = (xAverage*i + vec.x)/(i+1)
        yAverage = (yAverage*i + vec.y)/(i+1)
    }

    return VectorConverter(xAverage, yAverage)
}

fun Polygon.centroid(): Vector = this.path.centroid()

fun List<Vector>.toFloatArray(): FloatArray{
    val floatArray = FloatArray(this.size*3) {0f}
    this.forEachIndexed { i, v ->
        floatArray[3 * i] = v.x.toFloat()
        floatArray[3 * i + 1] = v.y.toFloat()
    }
    return floatArray
}

fun List<VectorConverter>.normalizeX(width: Double): List<VectorConverter> {
    val currentHeight = this.xRange()
    val factor = width/currentHeight
    this.scale(factor)
    return this
}


fun List<VectorConverter>.normalizeY(height: Double): List<VectorConverter> {
    val currentHeight = this.yRange()
    val factor = height/currentHeight
    this.scale(factor)
    return this
}

fun List<VectorConverter>.flipVertically(): List<VectorConverter> {
    this.forEach {
        it.y = -it.y
    }
    return this
}

fun List<VectorConverter>.toOrigin(): List<VectorConverter> {
    val minX = (this.minBy { it.x } ?: VectorConverter(0.0, 0.0)).x
    val minY = (this.minBy { it.y } ?: VectorConverter(0.0, 0.0)).y
    this.forEach {
        it.x = it.x - minX
        it.y = it.y - minY
    }
    return this
}

fun List<VectorConverter>.xRange(): Double {
    val max = this.maxBy { it.x } ?: VectorConverter(0.0, 0.0)
    val min = this.minBy { it.x } ?: VectorConverter(0.0, 0.0)
    return max.x - min.x
}

fun List<VectorConverter>.yRange(): Double {
    val max = this.maxBy { it.y } ?: VectorConverter(0.0, 0.0)
    val min = this.minBy { it.y } ?: VectorConverter(0.0, 0.0)
    return max.y - min.y
}

fun List<VectorConverter>.scale(factor: Double): List<VectorConverter> {
    this.forEach {
        it.x = it.x*factor
        it.y = it.y*factor
    }
    return this
}


fun List<VectorConverter>.scaleX(factor: Double): List<VectorConverter> {
    this.forEach {
        it.x = it.x*factor
    }
    return this
}


fun List<VectorConverter>.scaleY(factor: Double): List<VectorConverter> {
    this.forEach {
        it.y = it.y*factor
    }
    return this
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

fun vec(x: Number, y: Number): Vector {
    val xBits = java.lang.Float.floatToRawIntBits(x.toFloat())
    val yBits = java.lang.Float.floatToRawIntBits(y.toFloat())
    val xyBits = (yBits.toLong() shl 32 ushr 32) or (xBits.toLong() shl 32)
    return LightweightVector(xyBits)
}

fun vec(r: Number, theta: Angle): Vector {
    val x = r.toDouble()*cos(theta.toRadians().asDouble())
    val y = r.toDouble()*sin(theta.toRadians().asDouble())
    return vec(x, y)
}

fun vec(vec2: Vec2) = vec(vec2.x, vec2.y)

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

operator fun Vector.times(scale: Number) = vec(this.x.toFloat()*scale.toFloat(), this.y.toFloat()*scale.toFloat())

operator fun Vector.div(scale: Number) = vec(this.x.toFloat()/scale.toFloat(), this.y.toFloat()/scale.toFloat())

operator fun Vector.unaryMinus() = vec(-this.x.toFloat(), -this.y.toFloat())

infix fun Vector.equals(other: Vector) = this.x.toFloat() == other.x.toFloat() && this.y.toFloat() == other.y.toFloat()

fun Vector.normalize() = vec(1.0, this.theta)

fun Vector.toVec2() = Vec2(this.x.toFloat(), this.y.toFloat())
