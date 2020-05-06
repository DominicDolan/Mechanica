package com.mechanica.engine.memory.buffer

import com.mechanica.engine.memory.useMemoryStack
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer


inline fun IntArray.asBuffer(use: (IntBuffer) -> Unit) {
    useMemoryStack {
        val buffer = mallocInt(this.size)
        this@asBuffer.forEach { buffer.put(it) }
        buffer.flip()
        use(buffer)
    }
}

inline fun FloatArray.asBuffer(use: (FloatBuffer) -> Unit) {
    useMemoryStack {
        val buffer = mallocFloat(this.size)
        this@asBuffer.forEach { buffer.put(it) }
        buffer.flip()
        use(buffer)
    }
}

inline fun ShortArray.asBuffer(use: (ShortBuffer) -> Unit) {
    useMemoryStack {
        val buffer = mallocShort(this.size)
        this@asBuffer.forEach { buffer.put(it) }
        buffer.flip()
        use(buffer)
    }
}

fun ShortArray.toBuffer(): ShortBuffer {
    val buffer = BufferUtils.createShortBuffer(this.size)
    this.forEach { buffer.put(it) }
    buffer.flip()
    return buffer
}

fun IntArray.toBuffer(): IntBuffer {
    val buffer = BufferUtils.createIntBuffer(this.size)
    this.forEach { buffer.put(it) }
    buffer.flip()
    return buffer
}


fun FloatArray.toBuffer(): FloatBuffer {
    val buffer = BufferUtils.createFloatBuffer(this.size)
    this.forEach { buffer.put(it) }
    buffer.flip()
    return buffer
}

fun emptyFloatBuffer(size: Int): FloatBuffer {
    return BufferUtils.createFloatBuffer(size)
}

fun emptyShortBuffer(size: Int): ShortBuffer {
    return BufferUtils.createShortBuffer(size)
}

fun ShortBuffer.contentsToString(): String {
    val sb = StringBuilder()
    while (this.hasRemaining()) {
        sb.append(this.get()).append(", ")
    }
    return sb.toString()
}

fun FloatBuffer.contentsToString(): String {
    val sb = StringBuilder()
    while (this.hasRemaining()) {
        sb.append(this.get()).append(", ")
    }
    return sb.toString()
}