package com.mechanica.engine.shaders.utils

import com.mechanica.engine.shaders.buffers.ShortBufferObject
import com.mechanica.engine.shaders.models.Bindable

class ElementIndexArray(shorts: ShortArray) : Bindable {
    private val buffer = ShortBufferObject.createElementArrayBuffer(shorts)

    val vertexCount: Int
        get() = buffer.shorts.size

    constructor(numberOfQuads: Int) : this(createIndicesArrayForQuads(numberOfQuads))

    init {
        buffer.storeData(vertexCount.toLong())
    }

    override fun bind() {
        buffer.bind()
    }

    override fun unbind() {
        buffer.unbind()
    }

    fun redefineBuffer(shorts: ShortArray) {
        buffer.shorts = shorts

        buffer.storeData(shorts.size.toLong())
    }
}