package util.extensions

import compatibility.Vector
import org.jbox2d.common.Vec2
import kotlin.math.sqrt

/**
 * Created by domin on 02/11/2017.
 */

fun Vec2.hyp() = Math.hypot(x.toDouble(),y.toDouble())

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


operator fun Vec2.times(scale: Double) = this.set(this.x*scale.toFloat(), this.y*scale.toFloat())

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

fun Vec2.X() = this.x.toDouble()

fun Vec2.Y() = this.y.toDouble()

fun Vec2.midpoint(other: Vec2) : Vec2{
    return Vec2((this.x + other.x)/2f, (this.y + other.y)/2f)
}

fun Vec2.angle(): Double{
    return Math.atan2(this.Y(), this.X())
}

fun Vec2.rotate(angle: Double, center: Vec2){
    this -= center
    val r = this.hyp()
    val a = this.angle() + angle

    val newX = (r* Math.cos(a)).toFloat()
    val newY = (r* Math.sin(a)).toFloat()

    this.set(center.x + newX, center.y + newY)
}

fun Vec2.scale(scale: Double, center: Vec2){
    this -= center
    this *= scale
    this.set(center.x + this.x, center.y + this.y)
}

fun List<Vector>.centroid(): Vector {
    var xAverage = 0.0
    var yAverage = 0.0
    this.forEachIndexed { i, vec ->
        xAverage = (xAverage*i + vec.x)/(i+1)
        yAverage = (yAverage*i + vec.y)/(i+1)
    }

    return Vector(xAverage, yAverage)
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
    this.forEach {
        it.y = -it.y
    }
    return this
}

fun List<Vector>.toOrigin(): List<Vector> {
    val minX = (this.minBy { it.x } ?: Vector(0.0, 0.0)).x
    val minY = (this.minBy { it.y } ?: Vector(0.0, 0.0)).y
    this.forEach {
        it.x = it.x - minX
        it.y = it.y - minY
    }
    return this
}

fun List<Vector>.xRange(): Double {
    val max = this.maxBy { it.x } ?: Vector(0.0, 0.0)
    val min = this.minBy { it.x } ?: Vector(0.0, 0.0)
    return max.x - min.x
}

fun List<Vector>.yRange(): Double {
    val max = this.maxBy { it.y } ?: Vector(0.0, 0.0)
    val min = this.minBy { it.y } ?: Vector(0.0, 0.0)
    return max.y - min.y
}

fun List<Vector>.scale(factor: Double): List<Vector> {
    this.forEach {
        it.x = it.x*factor
        it.y = it.y*factor
    }
    return this
}


fun List<Vector>.scaleX(factor: Double): List<Vector> {
    this.forEach {
        it.x = it.x*factor
    }
    return this
}


fun List<Vector>.scaleY(factor: Double): List<Vector> {
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