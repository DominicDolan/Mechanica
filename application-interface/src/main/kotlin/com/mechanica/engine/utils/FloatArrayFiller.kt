package com.mechanica.engine.utils

import com.mechanica.engine.unit.vector.DynamicVector
import org.joml.Vector3f
import org.joml.Vector4f

class FloatArrayFiller(private val coordinateSize: Int) {
    var size: Int = 0
        private set
    var floats: FloatArray = FloatArray(100)
        private set

    fun fillFloats(array: FloatArray): FloatArray {
        size = array.size
        val floatArray = if (size <= floats.size) floats
        else FloatArray(array.size*2)

        floats = floatArray
        array.copyInto(floats)
        return floats
    }

    fun fillFloats(array: Array<DynamicVector>): FloatArray {
        return fillFloats(array) { v, i -> v.getOrZero(i).toFloat() }
    }

    fun fillFloats(array: Array<Vector3f>): FloatArray {
        return fillFloats(array) { v, i -> if (i < 3) v[i] else 0f }
    }

    fun fillFloats(array: Array<Vector4f>): FloatArray {
        return fillFloats(array) { v, i -> if (i < 4) v[i] else 0f }
    }

    private fun <T> fillFloats(array: Array<T>, converter: (T, Int) -> Float): FloatArray {
        size = array.size*coordinateSize
        val floatArray = if (size <= floats.size) floats
        else FloatArray(size*2)

        floats = floatArray

        return array.fillFloatArray(floatArray, coordinateSize, converter)
    }

    companion object {
        fun <T> Array<T>.fillFloatArray(floats: FloatArray, coordinateSize: Int, converter: (T, Int) -> Float): FloatArray {

            for (i in this.indices) {
                fun FloatArray.setAt(coordinate: Int, value: Float) {
                    if (coordinate < coordinateSize) this[i*coordinateSize + coordinate] = value
                }
                var coordinate = 0
                while (coordinate < coordinateSize) {
                    floats.setAt(coordinate, converter(this[i], coordinate++))
                }
            }
            return floats
        }
    }
}