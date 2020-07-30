package com.mechanica.engine.unit.vector

interface DynamicIntVector : IntVector {
    override var x: Int
    override var y: Int

    fun set(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun set(other: IntVector) = set(other.x, other.y)

    companion object {
        fun create(x: Int = 0, y: Int = 0) = object : DynamicIntVector {
            override var x = x
            override var y = y
        }
    }
}