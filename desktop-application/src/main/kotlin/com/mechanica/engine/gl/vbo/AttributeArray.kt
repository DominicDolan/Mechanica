package com.mechanica.engine.gl.vbo

import com.mechanica.engine.gl.vbo.pointer.AttributePointer
import org.lwjgl.opengl.GL20

class AttributeArray(
        array: FloatArray,
        attributePointer: AttributePointer) : VBO<FloatArray>(array, attributePointer) {

    constructor(vertexCount: Int, attributePointer: AttributePointer)
            : this(FloatArray(vertexCount*attributePointer.coordinateSize), attributePointer)

    override fun getArraySize(array: FloatArray): Int = array.size

    override fun bufferSubData(target: Int, offset: Long, array: FloatArray) {
        GL20.glBufferSubData(target, offset, array)
    }

    override fun initBufferData(target: Int, array: FloatArray) {
        GL20.glBufferData(target, array, GL20.GL_STATIC_DRAW)
    }

    override fun initBufferData(target: Int, arraySize: Int) {
        initBufferData(target, FloatArray(arraySize))
    }

}

