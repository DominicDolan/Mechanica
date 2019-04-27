package util.extensions

import org.jbox2d.common.Vec2

/**
 * Created by domin on 02/11/2017.
 */
operator fun Array<Vec2>.timesAssign(scale: Double){
    this.forEach { it *= scale }
}

inline operator fun <reified T> Array<T>.get(range: IntRange): Array<T>{
    return range.map { this[it] }.toTypedArray()
}