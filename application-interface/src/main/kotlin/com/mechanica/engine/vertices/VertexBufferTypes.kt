package com.mechanica.engine.vertices

import com.mechanica.engine.context.loader.MechanicaLoader


interface VertexBufferType<T> {
    fun createBuffer(array: T) : VertexBuffer<T>
}

interface ElementArrayType: VertexBufferType<ShortArray> {
    override fun createBuffer(array: ShortArray): IndexArray

    companion object : ElementArrayType by MechanicaLoader.shaderLoader.createElementArray()
}
