@file:Suppress("unused") // There will be many functions here that go unused most of the time

package util.extensions

import org.jbox2d.common.Vec2

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