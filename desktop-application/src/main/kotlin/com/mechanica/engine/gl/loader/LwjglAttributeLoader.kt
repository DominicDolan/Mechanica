package com.mechanica.engine.gl.loader

import com.mechanica.engine.color.Color
import com.mechanica.engine.gl.context.loader.attributes.AttributeLoader
import com.mechanica.engine.gl.vbo.LwjglVertexFloatBuffer
import com.mechanica.engine.gl.vbo.AttributeType
import com.mechanica.engine.gl.vbo.FloatAttributeType
import com.mechanica.engine.unit.vector.Vector
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL40

class LwjglAttributeLoader(private val location: Int) : AttributeLoader {

    override fun createFloatAttribute(coordinateSize: Int): FloatAttributeType {
        return LwjglFloatAttributeType(coordinateSize)
    }

    override fun createIntAttribute(coordinateSize: Int): AttributeType<IntArray> {
        TODO("not implemented")
    }

    override fun createShortAttribute(coordinateSize: Int): AttributeType<ShortArray> {
        TODO("not implemented")
    }

    private inner class LwjglFloatAttributeType(override val coordinateSize: Int) : FloatAttributeType {
        override val location = this@LwjglAttributeLoader.location
        private val glType = GL_FLOAT

        override fun bind() = bindAttributeArray(location, coordinateSize, glType)

        override fun createBuffer(array: FloatArray) = createBuffer(array.size) { set(array) }

        override fun createBuffer(array: Array<Vector>) = createBuffer(array.size) { set(array) }

        override fun createBuffer(array: Array<Vector3f>) = createBuffer(array.size) { set(array) }

        override fun createBuffer(array: Array<Vector4f>) = createBuffer(array.size) { set(array) }

        override fun createBuffer(array: Array<Color>) = createBuffer(array.size) { set(array) }

        private inline fun createBuffer(size: Int, setter: LwjglVertexFloatBuffer.() -> Unit): LwjglVertexFloatBuffer {
            val v = LwjglVertexFloatBuffer(size, GL40.GL_ARRAY_BUFFER, coordinateSize, this)
            setter(v)
            return v
        }
    }

    companion object {
        fun bindAttributeArray(location: Int, coordinateSize: Int, glType: Int) {
            GL40.glVertexAttribPointer(location, coordinateSize, glType, false, 0, 0)
            GL40.glEnableVertexAttribArray(location)
        }
    }

}