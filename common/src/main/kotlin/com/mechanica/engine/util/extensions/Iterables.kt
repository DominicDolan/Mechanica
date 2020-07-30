package com.mechanica.engine.util.extensions

import com.mechanica.engine.unit.vector.Vector

fun Iterable<Vector>.containsPoint(point: Vector): Boolean {
    var result = false
    var prev = this.last()
    
    for (vec in this){
        if ((vec.y > point.y) != (prev.y > point.y) &&
                (point.x < (prev.x - vec.x) * (point.y - vec.y) / (prev.y-vec.y) + vec.x)) {
            result = !result
        }
        prev = vec
    }
    return result
}

/**
 * A version of for each that should be more efficient for lists that
 * are fast at getting element with random access
 *
 * @param action action that should be performed on each element
 */
inline fun <E> List<E>.fori(action: (E) -> Unit) {
    for (i in this.indices) {
        action(this[i])
    }
}

inline fun <E> List<E>.foriIndexed(action: (E, Int) -> Unit) {
    for (i in this.indices) {
        action(this[i], i)
    }
}