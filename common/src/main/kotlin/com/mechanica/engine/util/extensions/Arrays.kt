@file:Suppress("unused") // There will be many functions here that go unused most of the time

package com.mechanica.engine.util.extensions

import com.mechanica.engine.memory.buffer.toBuffer
import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector
import java.nio.FloatBuffer

/**
 * Created by domin on 02/11/2017.
 */

fun <T> Array<T>.set(setter: (Int) -> T) {
    for (i in 0..this.size) {
        this[i] = setter(i)
    }
}


fun Array<Vector>.containsPoint(point: Vector): Boolean {
    val len = this.size
    var result = false

    for (i in 0 until len){
        val j = (i + len - 1)%len
        if ((this[i].y > point.y) != (this[j].y > point.y) &&
                (point.x < (this[j].x - this[i].x) * (point.y - this[i].y) / (this[j].y-this[i].y) + this[i].x)) {
            result = !result
        }
    }
    return result
}

fun Array<Vector>.toFloatBuffer(coordinateSize: Int): FloatBuffer {
    return this.toFloatArray(coordinateSize).toBuffer()
}

fun Array<Vector>.toFloatArray(coordinateSize: Int): FloatArray {
    val floats = FloatArray(this.size*coordinateSize)
    for (i in this.indices) {
        val floatsIndex = i*coordinateSize
        floats[floatsIndex] = this[i].x.toFloat()
        floats[floatsIndex+1] = this[i].y.toFloat()
        for (j in 2 until coordinateSize) {
            floats[floatsIndex + j] = 0f
        }
    }
    return floats
}

fun List<Vector>.toFloatBuffer(coordinateSize: Int): FloatBuffer {
    val floats = FloatArray(this.size*coordinateSize)
    for (i in this.indices) {
        val floatsIndex = i*coordinateSize
        floats[floatsIndex] = this[i].x.toFloat()
        floats[floatsIndex+1] = this[i].y.toFloat()
        for (j in 2 until coordinateSize) {
            floats[floatsIndex + j] = 1f
        }
    }
    return floats.toBuffer()
}

inline operator fun <reified T> Array<T>.get(range: IntRange): Array<T>{
    return range.map { this[it] }.toTypedArray()
}

fun Array<Float>.mean(): Float {
    return this.sum()/this.size
}

fun List<Float>.mean(): Float {
    return this.sum()/this.size
}


fun List<Double>.mean(): Double {
    return this.sum()/this.size
}


fun FloatArray.fill(arrayList: List<Vector>, start: Int = 0, end: Int = (arrayList.size + start)) {
    for (i in start until end) {
        this[i*3] = arrayList[i].x.toFloat()
        this[i*3 + 1] = arrayList[i].y.toFloat()
    }
}

fun FloatArray.fill(arrayList: Array<Vector>, start: Int = 0, end: Int = (arrayList.size + start)) {
    for (i in start until end) {
        this[i*3] = arrayList[i].x.toFloat()
        this[i*3 + 1] = arrayList[i].y.toFloat()
    }
}

fun FloatArray.fill(arrayList: Array<LightweightVector>, start: Int = 0, end: Int = (arrayList.size + start)) {
    for (i in start until end) {
        this[i*3] = arrayList[i].x.toFloat()
        this[i*3 + 1] = arrayList[i].y.toFloat()
    }
}

fun FloatArray.fillLightWeight(iterator: Iterator<LightweightVector>) {
    for ((i, v) in iterator.withIndex()) {
        this[i*3] = v.x.toFloat()
        this[i*3 + 1] = v.y.toFloat()
    }
}

fun FloatArray.fill(iterator: Iterator<Vector>) {
    for ((i, v) in iterator.withIndex()) {
        this[i*3] = v.x.toFloat()
        this[i*3 + 1] = v.y.toFloat()
    }
}

fun List<*>.indexLooped(index: Int): Int {
    return when {
        index < 0 -> this.size + index
        index > this.lastIndex -> index - this.size
        else -> index
    }
}

fun List<*>.indexConstrained(index: Int): Int {
    return when {
        index < 0 -> 0
        index > this.lastIndex -> this.lastIndex
        else -> index
    }
}
