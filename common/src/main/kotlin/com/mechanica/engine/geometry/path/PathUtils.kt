package com.mechanica.engine.geometry.path

import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.MutableVector2
import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.vec
import kotlin.math.sqrt

fun Iterable<MutableVector2>.moveToOrigin() {
    val minX = (this.minByOrNull { it.x } ?: vec(0.0, 0.0)).x
    val minY = (this.minByOrNull { it.y } ?: vec(0.0, 0.0)).y
    this.forEach { vec ->
        vec.x -= minX
        vec.y -= minY
    }
}

fun List<InlineVector>.moveToOrigin(): List<InlineVector> {
    val minX = (this.minByOrNull { it.x } ?: vec(0.0, 0.0)).x
    val minY = (this.minByOrNull { it.y } ?: vec(0.0, 0.0)).y
    return this.map { vec(it.x - minX, it.y - minY) }
}

fun Iterable<MutableVector2>.scale(scale: Double) = scale(scale, scale)

fun Iterable<MutableVector2>.scale(scaleX: Double, scaleY: Double) {
    this.forEach { vec ->
        vec.x *= scaleX
        vec.y *= scaleY
    }
}

fun List<InlineVector>.scale(scale: Double) = scale(scale, scale)

fun List<InlineVector>.scale(scaleX: Double, scaleY: Double): List<InlineVector> {
    return this.map { vec(it.x*scaleX, it.y*scaleY) }
}


fun Iterable<Vector2>.centroid(): InlineVector {
    var xAverage = 0.0
    var yAverage = 0.0
    this.forEachIndexed { i, vec ->
        xAverage = (xAverage*i + vec.x)/(i+1)
        yAverage = (yAverage*i + vec.y)/(i+1)
    }

    return vec(xAverage, yAverage)
}

fun Iterable<MutableVector2>.normalizeX(width: Double): Iterable<MutableVector2> {
    val currentHeight = this.xRange()
    val factor = width/currentHeight
    this.scale(factor)
    return this
}


fun Iterable<MutableVector2>.normalizeY(height: Double) {
    val currentHeight = this.yRange()
    val factor = height/currentHeight
    this.scale(factor)
}

fun Iterable<MutableVector2>.flipVertically() {
    this.forEach {
        it.y = -it.y
    }
}

fun Iterable<InlineVector>.flipVertically() = this.map { vec(it.x, -it.y) }

fun Iterable<MutableVector2>.xRange(): Double {
    val max = this.maxByOrNull { it.x } ?: vec(0.0, 0.0)
    val min = this.minByOrNull { it.x } ?: vec(0.0, 0.0)
    return max.x - min.x
}

fun Iterable<MutableVector2>.yRange(): Double {
    val max = this.maxByOrNull { it.y } ?: vec(0.0, 0.0)
    val min = this.minByOrNull { it.y } ?: vec(0.0, 0.0)
    return max.y - min.y
}

fun Iterable<MutableVector2>.calculateLength(): Double {
    var previous = this.first()
    var length = 0.0
    for (vector in this) {
        val x = vector.x - previous.x
        val y = vector.y - previous.y
        length += sqrt(x*x + y*y)
        previous = vector
    }
    return length
}