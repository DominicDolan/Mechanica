package com.mechanica.engine.game.view

import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.Vector


fun View.contains(x: Double, y: Double): Boolean {
    return x > left && x < right && y < top && y > bottom
}

fun View.contains(point: InlineVector) = contains(point.x, point.y)
fun View.contains(point: Vector) = contains(point.x, point.y)

fun View.intersects(other: View): Boolean {
    return other.contains(left, top)
            || other.contains(left, bottom)
            || other.contains(right, top)
            || other.contains(right, bottom)
}