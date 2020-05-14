package com.mechanica.engine.vertices

import com.mechanica.engine.context.loader.GLLoader


interface VertexBufferType<T> {
    fun createBuffer(array: T) : VertexBuffer<T>
}

interface ElementArrayType: VertexBufferType<ShortArray> {
    override fun createBuffer(array: ShortArray): ElementArrayBuffer

    companion object : ElementArrayType by GLLoader.createElementArray()
}
