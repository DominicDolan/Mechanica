package com.mechanica.engine.shaders.buffers

import com.mechanica.engine.shaders.context.ShaderFactory
import com.mechanica.engine.shaders.glPrimitives.GLPrimitive
import com.mechanica.engine.shaders.models.Bindable


interface GLBufferObject : Bindable {
    val id: Int
    val type: GLPrimitive

    fun storeData(arraySize: Long) {
        bind()
        val capacityNeeded = arraySize*type.byteSize
        if (getExistingBufferSize() < capacityNeeded) {
            initiateBuffer(capacityNeeded)
        }
        storeSubData(0)
    }

    fun initiateBuffer(byteCapacity: Long)
    fun storeSubData(offset: Long)
    fun getExistingBufferSize(): Long
}

abstract class FloatBufferObject : GLBufferObject {
    abstract var floats: FloatArray
    override val type = GLPrimitive.float()

    companion object {
        fun createArrayBuffer(floats: FloatArray): FloatBufferObject {
            return ShaderFactory.bufferFactories.arrayBufferFactory.floats(floats)
        }
    }
}

abstract class ShortBufferObject : GLBufferObject {
    abstract var shorts: ShortArray
    override val type: GLPrimitive = GLPrimitive.short()

    companion object {
        fun createArrayBuffer(shorts: ShortArray): ShortBufferObject {
            return ShaderFactory.bufferFactories.arrayBufferFactory.shorts(shorts)
        }

        fun createElementArrayBuffer(shorts: ShortArray): ShortBufferObject {
            return ShaderFactory.bufferFactories.elementArrayBufferFactory.shorts(shorts)
        }
    }
}

abstract class IntBufferObject : GLBufferObject {
    abstract var ints: IntArray
    override val type: GLPrimitive = GLPrimitive.short()

    companion object {
        fun createArrayBuffer(ints: IntArray): IntBufferObject {
            return ShaderFactory.bufferFactories.arrayBufferFactory.ints(ints)
        }
    }
}