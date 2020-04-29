package com.mechanica.engine.gl.vbo

import com.mechanica.engine.util.extensions.toFloatArray
import com.mechanica.engine.gl.utils.createTextureUnitSquareVecArray
import com.mechanica.engine.gl.utils.createUnitSquareFloatArray
import com.mechanica.engine.gl.vbo.pointer.AttributePointer
import com.mechanica.engine.gl.vbo.pointer.VBOPointer
import org.lwjgl.opengl.GL40.*
import org.lwjgl.system.MemoryUtil.memAlloc
import org.lwjgl.system.MemoryUtil.memFree

@Suppress("LeakingThis") // The State of the VBO is set before any leaking occurs
abstract class VBO<T> protected constructor(array: T, private val pointer: VBOPointer): Bindable {
    private var capacity: Int
    val id: Int = glGenBuffers()
    var vertexCount: Int

    init {
        glBindBuffer(pointer.bufferType, id)
        vertexCount = getArraySize(array)/pointer.coordinateSize
        capacity = vertexCount
        initBufferData(pointer.bufferType, array)
    }

    override fun bind() {
        glBindBuffer(pointer.bufferType, id)
        if (pointer is AttributePointer) {
            pointer.enable()
        }
    }

    fun update(array: T, from: Int = 0, length: Int = getArraySize(array)/pointer.coordinateSize) {
        val cs = pointer.coordinateSize
        val byteOffset = from*cs*pointer.variableSize

        if (length + from > capacity) {
            increaseSize(length + from)
        }
        this.vertexCount = length

        glBindBuffer(pointer.bufferType, id)
        bufferSubData(pointer.bufferType, byteOffset.toLong(), array)
    }

    protected abstract fun getArraySize(array: T): Int

    protected abstract fun bufferSubData(target: Int, offset: Long, array: T)
    protected abstract fun initBufferData(target: Int, array: T)
    protected abstract fun initBufferData(target: Int, arraySize: Int)

    private fun increaseSize(newSize: Int) {
        val buffer = memAlloc(pointer.coordinateSize*pointer.variableSize*capacity)
        capacity = newSize*2

        glBindBuffer(pointer.bufferType, id)

        glGetBufferSubData(pointer.bufferType, 0, buffer)
        initBufferData(pointer.bufferType, capacity*pointer.coordinateSize)
        glBufferSubData(pointer.bufferType, 0, buffer)

        memFree(buffer)
    }

    companion object {
        fun createUnitSquarePositionAttribute(): AttributeArray {
            return AttributeArray(createUnitSquareFloatArray(), VBOPointer.position)
        }

        fun createUnitSquareTextureAttribute(): AttributeArray {
            return AttributeArray(createTextureUnitSquareVecArray().toFloatArray(2), VBOPointer.texCoords)
        }
    }

}