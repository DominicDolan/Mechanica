@file:Suppress("unused") // There will be many functions here that go unused most of the time
package com.mechanica.engine.util.extensions

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

fun Number.isHigher(other: Number): Boolean {
    return this.toDouble() > other.toDouble()
}

fun Number.isHigher(other1: Number, other2: Number): Boolean {
    return this.isHigher(other1) && this.isHigher(other2)
}

fun Number.isHigher(other1: Number, other2: Number, other3: Number): Boolean {
    return this.isHigher(other1) && this.isHigher(other2) && this.isHigher(other3)
} 

fun Number.isHigher(other1: Number, other2: Number, other3: Number, vararg others: Number): Boolean {
    var isHigher = false
    if (this.isHigher(other1) && this.isHigher(other2) && this.isHigher(other3)){
        isHigher = true
    }
    for (number in others) {
        if (this.isHigher(number)) {
            isHigher = true
        }
    }
    return isHigher
}

fun Number.isLower(other: Number): Boolean {
    return this.toDouble() < other.toDouble()
}

fun Number.isLower(other1: Number, other2: Number): Boolean {
    return this.isLower(other1) && this.isLower(other2)
}

fun Number.isLower(other1: Number, other2: Number, other3: Number): Boolean {
    return this.isLower(other1) && this.isLower(other2) && this.isLower(other3)
} 

fun Number.isLower(other1: Number, other2: Number, other3: Number, vararg others: Number): Boolean {
    var isLower = false
    if (this.isLower(other1) && this.isLower(other2) && this.isLower(other3)){
        isLower = true
    }
    for (number in others) {
        if (this.isLower(number)) {
            isLower = true
        }
    }
    return isLower
}

fun Double.constrain(lower: Double, higher: Double): Double {
    val sign = sign(higher - lower)
    if (sign == 0.0) return lower

    val restrainLower = max(sign*this, sign*lower)
    return sign*min(restrainLower, sign*higher)
}

fun Double.constrainLooped(lower: Double, higher: Double): Double {
    val diff = (higher - lower)
    val sign = sign(diff)

    if (sign == 0.0) return lower

    return when {
        sign*this < sign*lower -> higher + sign * (sign * this - lower) % diff
        sign*this > sign*higher -> lower + sign * (sign * this - higher) % diff
        else -> this
    }
}

fun main() {
    val num = (7.0).constrain(5.0, 5.0)
    println(num)
}

fun Int.constrain(lower: Int, higher: Int): Int {
    val restrainLower = max(this, lower)
    return min(restrainLower, higher)
}

fun Int.constrain(range: IntRange): Int {
    val restrainLower = max(this, range.first)
    return min(restrainLower, range.last)
}