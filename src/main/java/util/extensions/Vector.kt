package util.extensions

import org.jbox2d.common.Vec2

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