package com.mechanica.engine.unit.vector

class VectorArray(size: Int, init: (Int) -> InlineVector) {
    private val longArray: LongArray = LongArray(size) { init(it).toLong() }

    val size: Int
        get() = longArray.size

    operator fun get(index: Int) = InlineVector(longArray[index])

    operator fun set(index: Int, vector: InlineVector) {
        longArray[index] = vector.toLong()
    }

    fun forEach(block: (InlineVector) -> Unit) {
        for (l in longArray) {
            block(InlineVector(l))
        }
    }
}