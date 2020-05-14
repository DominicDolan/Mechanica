package com.mechanica.engine.shader.vbo

import com.mechanica.engine.memory.useMemoryStack
import com.mechanica.engine.vertices.VertexBuffer
import org.lwjgl.opengl.GL40.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

@Suppress("LeakingThis") // The state of the class is set before any leaking occurs
abstract class LwjglVertexBuffer<T>(var byteCapacity: Int, val bufferType: Int) : VertexBuffer<T> {
    override val id = glGenBuffers()

    init {
        glBindBuffer(bufferType, id)

        useMemoryStack {
            val buffer = this.calloc(byteCapacity)
            glBufferData(bufferType, buffer, GL_STATIC_DRAW)

        }
    }

    override fun bind() {
        glBindBuffer(bufferType, id)
    }

    override fun set(array: T, from: Int, length: Int) {
        val byteSize = array.variableByteSize
        val newCapacity = (from + length)*byteSize
        if (newCapacity > byteCapacity) {
            increaseSize(newCapacity*2)
        } else {
            bind()
        }

        subData((byteSize * from).toLong(), array)
    }

    inline fun <R> set(array: Array<R>, bytesPerValue: Int, start: Int = 0, length: Int = array.size, operation: (R, ByteBuffer) -> Unit) {
        val byteLength = length*bytesPerValue
        val byteStart = start*bytesPerValue
        val newCapacity = (byteStart + byteLength)

        if (newCapacity > byteCapacity) {
            increaseSize(newCapacity*2)
        } else {
            bind()
        }

        useMemoryStack {
            val buffer = calloc(byteLength)

            for (i in 0 until length) {
                operation(array[i], buffer)
            }
            buffer.flip()
            glBufferSubData(bufferType, byteStart.toLong(), buffer)
        }
    }

    fun increaseSize(newByteSize: Int) {
        glBindBuffer(bufferType, id)
        val buffer = MemoryUtil.memAlloc(newByteSize)

        buffer.limit(byteCapacity)

        glGetBufferSubData(bufferType, 0, buffer)

        buffer.limit(newByteSize)
        byteCapacity = newByteSize

        glBufferData(bufferType, buffer, GL_STATIC_DRAW)

        MemoryUtil.memFree(buffer)
    }

    abstract val T.variableByteSize: Int

    abstract fun subData(offset: Long, array: T)
}