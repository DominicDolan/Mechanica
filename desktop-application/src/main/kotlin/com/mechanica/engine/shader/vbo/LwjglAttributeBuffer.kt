package com.mechanica.engine.shader.vbo

import com.mechanica.engine.color.Color
import com.mechanica.engine.shader.vars.attributes.AttributeDefinition
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.util.extensions.constrain
import com.mechanica.engine.vertices.AttributeArray
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL15.glBufferSubData
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL40
import java.nio.ByteBuffer

class LwjglAttributeBuffer(
        size: Int,
        definition: AttributeDefinition)
    : LwjglVertexBuffer<FloatArray>(size*definition.type.coordinateSize*4, GL40.GL_ARRAY_BUFFER),
        AttributeArray {

    override val FloatArray.valueCount: Int get() = this.size
    override val FloatArray.variableByteSize: Int get() = 4

    override var vertexCount = size

    private val coordinateSize: Int = definition.type.coordinateSize
    private val coordinateValues = FloatArray(16)

    private val glType = GL_FLOAT
    private val location = definition.qualifier.location

    override fun bind() {
        super.bind()
        GL40.glVertexAttribPointer(location, coordinateSize, glType, false, 0, 0)
        GL40.glEnableVertexAttribArray(location)
    }

    override fun unbind() {
        GL20.glDisableVertexAttribArray(location)
    }

    override fun subData(offset: Long, array: FloatArray) = glBufferSubData(bufferTarget, offset, array)

    override fun set(array: Array<out Vector>, from: Int, length: Int) {
        vertexCount = array.size
        set(array, coordinateSize*4, length = length) {vec, buffer ->
            var i =0
            coordinateValues[i++] = vec.x.toFloat()
            coordinateValues[i++] = vec.y.toFloat()
            buffer.putCoordinates(coordinateValues, i)
        }
    }

    override fun set(array: Array<Vector3f>, from: Int, length: Int) {
        vertexCount = array.size
        set(array, coordinateSize*4, length = length) {vec, buffer ->
            var i =0
            coordinateValues[i++] = vec.x
            coordinateValues[i++] = vec.y
            coordinateValues[i++] = vec.z
            buffer.putCoordinates(coordinateValues, i)
        }
    }

    override fun set(array: Array<Vector4f>, from: Int, length: Int) {
        vertexCount = array.size
        set(array, coordinateSize*4, length = length) {vec, buffer ->
            var i =0
            coordinateValues[i++] = vec.x
            coordinateValues[i++] = vec.y
            coordinateValues[i++] = vec.z
            coordinateValues[i++] = vec.w
            buffer.putCoordinates(coordinateValues, i)
        }
    }

    override fun set(array: Array<out Color>, from: Int, length: Int) {
        vertexCount = array.size
        set(array, coordinateSize*4, length = length) {color, buffer ->
            var i = 0
            coordinateValues[i++] = color.r.toFloat()
            coordinateValues[i++] = color.g.toFloat()
            coordinateValues[i++] = color.b.toFloat()
            coordinateValues[i++] = color.a.toFloat()
            buffer.putCoordinates(coordinateValues, i)
        }
    }


    private fun ByteBuffer.putCoordinates(coordinateValues: FloatArray, valueCount: Int) {
        val putCount = valueCount.constrain(0, coordinateSize)
        for (i in 0 until putCount) {
            this.putFloat(coordinateValues[i])
        }
        val byteAdvance = (coordinateSize - putCount)*4
        position(position() + byteAdvance)

    }

}