package com.mechanica.engine.gl.vbo

import com.mechanica.engine.gl.Bindable
import com.mechanica.engine.memory.useMemoryStack
import org.lwjgl.opengl.GL40.*
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

@Suppress("LeakingThis") // The state of the class is set before any leaking occurs with the exception of 'type' which has a null check
abstract class LwjglVertexBuffer<T>(var byteCapacity: Int, val bufferType: Int) : VertexBuffer<T> {
    override val id = glGenBuffers()
    init {
        bind()

        useMemoryStack {
            val buffer = this.calloc(byteCapacity)
            glBufferData(bufferType, buffer, GL_STATIC_DRAW)

        }
    }

    @Suppress("UNNECESSARY_SAFE_CALL")// It's possible that 'bind()' will be called before 'type' has been initialized
    override fun bind() {
        glBindBuffer(bufferType, id)

        val type = this.type
        if (type is Bindable) type?.bind()
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

    inline fun <R> set(array: Array<R>, byteLength: Int, byteOffset: Long = 0, operation: (R, ByteBuffer) -> Unit) {
        val newCapacity = (byteOffset + byteLength).toInt()
        if (newCapacity > byteCapacity) {
            increaseSize(newCapacity*2)
        } else {
            bind()
        }

        useMemoryStack {
            val buffer = malloc(byteLength)
            for (e in array) {
                operation(e, buffer)
            }
            buffer.flip()
            glBufferSubData(bufferType, byteOffset, buffer)
        }
    }

    fun increaseSize(newByteSize: Int) {
        val buffer = MemoryUtil.memAlloc(newByteSize)
        byteCapacity = newByteSize

        glBindBuffer(bufferType, id)
        glGetBufferSubData(bufferType, 0, buffer)
        glBufferData(bufferType, buffer, GL_STATIC_DRAW)

        MemoryUtil.memFree(buffer)
    }

    abstract val T.variableByteSize: Int

    abstract fun subData(offset: Long, array: T)
}