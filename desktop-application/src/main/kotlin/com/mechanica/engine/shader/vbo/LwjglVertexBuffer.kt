package com.mechanica.engine.shader.vbo

import com.mechanica.engine.memory.useMemoryStack
import com.mechanica.engine.vertices.VertexBuffer
import org.lwjgl.opengl.GL40.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

abstract class LwjglVertexBuffer<T>(var byteCapacity: Int, val bufferTarget: Int) : VertexBuffer<T> {
    final override val id = glGenBuffers()

    init {
        glBindBuffer(bufferTarget, id)

        glBufferData(bufferTarget, byteCapacity.toLong(), GL_STATIC_DRAW)
    }

    override fun bind() {
        glBindBuffer(bufferTarget, id)
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

        if (byteLength > 0) {
            useMemoryStack {
                val buffer = calloc(byteLength)

                for (i in 0 until length) {
                    operation(array[i], buffer)
                }
                buffer.flip()
                glBufferSubData(bufferTarget, byteStart.toLong(), buffer)
            }
        }
    }

    fun increaseSize(newByteSize: Int) {
        glBindBuffer(bufferTarget, id)
        val buffer = MemoryUtil.memAlloc(newByteSize)

        if (byteCapacity > 0) {
            buffer.limit(byteCapacity)

            glGetBufferSubData(bufferTarget, 0, buffer)
        }

        buffer.limit(newByteSize)
        byteCapacity = newByteSize

        glBufferData(bufferTarget, buffer, GL_DYNAMIC_DRAW)

        MemoryUtil.memFree(buffer)
    }

    abstract val T.variableByteSize: Int

    abstract fun subData(offset: Long, array: T)
}