package com.mechanica.engine.shaders.buffers

import com.mechanica.engine.shaders.context.GLBufferFactory

interface BufferFactories {
    val arrayBufferFactory: GLBufferFactory
    val elementArrayBufferFactory: GLBufferFactory
    val copyReadBufferFactory: GLBufferFactory
    val copyWriteBufferFactory: GLBufferFactory
}