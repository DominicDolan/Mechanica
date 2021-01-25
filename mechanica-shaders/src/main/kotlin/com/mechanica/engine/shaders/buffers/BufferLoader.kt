package com.mechanica.engine.shaders.buffers

import com.mechanica.engine.shaders.context.GLBufferFactory
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer


interface BufferLoader {
    fun byteBuffer(size: Int): ByteBuffer
    fun intBuffer(size: Int): IntBuffer
    fun floatBuffer(size: Int): FloatBuffer

    fun useByteBuffer(size: Int, block: ByteBuffer.() -> Unit)

    val arrayBufferFactory: GLBufferFactory
    val elementArrayBufferFactory: GLBufferFactory
    val copyReadBufferFactory: GLBufferFactory
    val copyWriteBufferFactory: GLBufferFactory
}