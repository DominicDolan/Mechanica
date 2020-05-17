package com.mechanica.engine.unit.vector

class VectorArray(size: Int, init: (Int) -> LightweightVector) {
    private val longArray: LongArray = LongArray(size) { init(it).toLong() }

    val size: Int
        get() = longArray.size

    operator fun get(index: Int) = LightweightVector(longArray[index])

    operator fun set(index: Int, vector: LightweightVector) {
        longArray[index] = vector.toLong()
    }

    fun forEach(block: (LightweightVector) -> Unit) {
        for (l in longArray) {
            block(LightweightVector(l))
        }
    }
}