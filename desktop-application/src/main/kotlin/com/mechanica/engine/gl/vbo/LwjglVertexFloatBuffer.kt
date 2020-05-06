package com.mechanica.engine.gl.vbo

import com.mechanica.engine.color.Color
import com.mechanica.engine.unit.vector.Vector
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL15.glBufferSubData

class LwjglVertexFloatBuffer(
        size: Int, bufferType: Int,
        private val coordinateSize: Int,
        override val type: VertexBufferType<FloatArray>) : LwjglVertexBuffer<FloatArray>(size*coordinateSize*4, bufferType), VertexFloatArray {

    override val FloatArray.valueCount: Int get() = this.size
    override val FloatArray.variableByteSize: Int get() = 4

    override fun subData(offset: Long, array: FloatArray) = glBufferSubData(bufferType, offset, array)

    override fun set(array: Array<Vector>, from: Int, length: Int) {
        if (coordinateSize >= 1) {
            set(array, length*coordinateSize*4) { vec, buffer ->
                buffer.putFloat(vec.x.toFloat())
                if (coordinateSize >= 2) {
                    buffer.putFloat(vec.y.toFloat())
                }
                if (coordinateSize >= 3) {
                    for (i in 3 until coordinateSize) {
                        val currentPosition = buffer.position()
                        buffer.position(currentPosition + 4)
                    }
                }
            }
        }

        when(coordinateSize) {
            1, 2 -> set(array, length*2*4) {vec, buffer ->
                buffer.putFloat(vec.x.toFloat())
                buffer.putFloat(vec.y.toFloat())
            }
            else -> TODO("not implemented")
        }
    }

    override fun set(array: Array<Vector3f>, from: Int, length: Int) {
        TODO("not implemented")
    }

    override fun set(array: Array<Vector4f>, from: Int, length: Int) {
        TODO("not implemented")
    }

    override fun set(array: Array<Color>, from: Int, length: Int) {
        if (coordinateSize == 4) {
            set(array, length*coordinateSize*4) { c, buffer ->
                buffer.putFloat(c.r.toFloat())
                buffer.putFloat(c.g.toFloat())
                buffer.putFloat(c.b.toFloat())
                buffer.putFloat(c.a.toFloat())
            }
        }
    }
}