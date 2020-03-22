@file:Suppress("unused") // There will be many functions here that go unused most of the time

package util.extensions

import loader.toBuffer
import org.jbox2d.common.Vec2
import util.units.Vector
import java.nio.FloatBuffer

/**
 * Created by domin on 02/11/2017.
 */
operator fun Array<Vec2>.timesAssign(scale: Double){
    this.forEach { it *= scale }
}

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


fun FloatArray.fill(arrayList: ArrayList<Vector>, start: Int = 0, end: Int = (arrayList.size + start)) {
    for (i in start until end) {
        this[i*3] = arrayList[i].x.toFloat()
        this[i*3 + 1] = arrayList[i].y.toFloat()
    }
}
