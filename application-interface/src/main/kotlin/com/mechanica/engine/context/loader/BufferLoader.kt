package com.mechanica.engine.context.loader

import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

interface BufferLoader {
    fun byteBuffer(size: Int): ByteBuffer
    fun intBuffer(size: Int): IntBuffer
    fun floatBuffer(size: Int): FloatBuffer

    fun useByteBuffer(size: Int, block: ByteBuffer.() -> Unit)

    val arrayBufferLoaders: GLBufferLoaders
    val elementArrayBufferLoaders: GLBufferLoaders
    val copyReadBufferLoaders: GLBufferLoaders
    val copyWriteBufferLoaders: GLBufferLoaders
}