package com.mechanica.engine.input

class DirectionalKeys(val negativeKey: Key, val positiveKey: Key, private val multiplier: Double = 1.0) {

    val value: Double
        get() {
            return when {
                positiveKey() && negativeKey() -> 0.0
                negativeKey() -> -1.0*multiplier
                positiveKey() -> 1.0*multiplier
                else -> 0.0
            }
        }
}