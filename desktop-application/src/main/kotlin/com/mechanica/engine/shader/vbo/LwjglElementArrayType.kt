package com.mechanica.engine.shader.vbo

import com.mechanica.engine.vertices.ElementArrayBuffer
import com.mechanica.engine.vertices.ElementArrayType
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL40.*

class LwjglElementArrayType : ElementArrayType {
    override fun createBuffer(array: ShortArray) = object : ElementArrayBuffer {
        override val id = glGenBuffers()
        override var vertexCount = array.size
        override val ShortArray.valueCount: Int get() = size
        private val bufferTarget = GL_ELEMENT_ARRAY_BUFFER

        init {
            glBindBuffer(bufferTarget, id)
            glBufferData(bufferTarget, array, GL20.GL_STATIC_DRAW)
        }

        override fun set(array: ShortArray, from: Int, length: Int) {
            glBufferSubData(bufferTarget, (from*2).toLong(), array)
        }

        override fun bind() = glBindBuffer(bufferTarget, id)

    }
}