package com.mechanica.engine.unit.vector

interface DynamicVector : Vector {
    override var x: Double
    override var y: Double

    fun set(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    fun set(vector: Vector) = set(vector.x, vector.y)
    fun set(vector: LightweightVector) = set(vector.x, vector.y)

    companion object {
        fun create(x: Double = 0.0, y: Double = 0.0) = object : DynamicVector {
            override var x = x
            override var y = y
            override fun toString() = Companion.toString(this)
        }

        fun create(vector: LightweightVector) = object : DynamicVector {
            override var x = vector.x
            override var y = vector.y
            override fun toString() = Companion.toString(this)
        }

        fun create(vector: Vector) = object : DynamicVector {
            override var x = vector.x
            override var y = vector.y
            override fun toString() = Companion.toString(this)
        }

        fun toString(vector: DynamicVector) = "(${vector.x}, ${vector.y})"
    }
}