package com.mechanica.engine.shader.vbo

import com.mechanica.engine.vertices.IndexArray
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL40

class LwjglIndexArray(array: ShortArray) : LwjglVertexBuffer<ShortArray>(array.size*2, GL_ELEMENT_ARRAY_BUFFER), IndexArray {
    override val ShortArray.variableByteSize: Int get() = 2
    override val ShortArray.valueCount: Int get() = size

    override var vertexCount = array.size

    init {
        glBindBuffer(bufferTarget, id)
        GL40.glBufferData(bufferTarget, array, GL20.GL_STATIC_DRAW)
    }

    override fun subData(offset: Long, array: ShortArray) = glBufferSubData(bufferTarget, offset, array)

    override fun set(array: ShortArray, from: Int, length: Int) {
        vertexCount = array.size
        super.set(array, from, length)
    }
}