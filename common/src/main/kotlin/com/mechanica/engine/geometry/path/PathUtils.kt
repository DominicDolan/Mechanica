package com.mechanica.engine.geometry.path

import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec
import kotlin.math.sqrt

fun Iterable<DynamicVector>.moveToOrigin() {
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

fun Iterable<DynamicVector>.scale(scale: Double) = scale(scale, scale)

fun Iterable<DynamicVector>.scale(scaleX: Double, scaleY: Double) {
    this.forEach { vec ->
        vec.x *= scaleX
        vec.y *= scaleY
    }
}

fun List<InlineVector>.scale(scale: Double) = scale(scale, scale)

fun List<InlineVector>.scale(scaleX: Double, scaleY: Double): List<InlineVector> {
    return this.map { vec(it.x*scaleX, it.y*scaleY) }
}


fun Iterable<Vector>.centroid(): InlineVector {
    var xAverage = 0.0
    var yAverage = 0.0
    this.forEachIndexed { i, vec ->
        xAverage = (xAverage*i + vec.x)/(i+1)
        yAverage = (yAverage*i + vec.y)/(i+1)
    }

    return vec(xAverage, yAverage)
}

fun Iterable<DynamicVector>.normalizeX(width: Double): Iterable<DynamicVector> {
    val currentHeight = this.xRange()
    val factor = width/currentHeight
    this.scale(factor)
    return this
}


fun Iterable<DynamicVector>.normalizeY(height: Double) {
    val currentHeight = this.yRange()
    val factor = height/currentHeight
    this.scale(factor)
}

fun Iterable<DynamicVector>.flipVertically() {
    this.forEach {
        it.y = -it.y
    }
}

fun Iterable<InlineVector>.flipVertically() = this.map { vec(it.x, -it.y) }

fun Iterable<DynamicVector>.xRange(): Double {
    val max = this.maxByOrNull { it.x } ?: vec(0.0, 0.0)
    val min = this.minByOrNull { it.x } ?: vec(0.0, 0.0)
    return max.x - min.x
}

fun Iterable<DynamicVector>.yRange(): Double {
    val max = this.maxByOrNull { it.y } ?: vec(0.0, 0.0)
    val min = this.minByOrNull { it.y } ?: vec(0.0, 0.0)
    return max.y - min.y
}

fun Iterable<DynamicVector>.calculateLength(): Double {
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