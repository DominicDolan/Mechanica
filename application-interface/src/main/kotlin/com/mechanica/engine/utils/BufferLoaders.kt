package com.mechanica.engine.utils

import com.mechanica.engine.context.loader.MechanicaLoader
import com.mechanica.engine.models.Bindable


interface GLBufferLoader : Bindable {
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

abstract class FloatBufferLoader : GLBufferLoader {
    abstract var floats: FloatArray
    override val type = GLPrimitive.float()

    companion object {
        fun createArrayBuffer(floats: FloatArray): FloatBufferLoader {
            return MechanicaLoader.bufferLoader.arrayBufferLoaders.floats(floats)
        }
    }
}

abstract class ShortBufferLoader : GLBufferLoader {
    abstract var shorts: ShortArray
    override val type: GLPrimitive = GLPrimitive.short()

    companion object {
        fun createArrayBuffer(shorts: ShortArray): ShortBufferLoader {
            return MechanicaLoader.bufferLoader.arrayBufferLoaders.shorts(shorts)
        }

        fun createElementArrayBuffer(shorts: ShortArray): ShortBufferLoader {
            return MechanicaLoader.bufferLoader.elementArrayBufferLoaders.shorts(shorts)
        }
    }
}

abstract class IntBufferLoader : GLBufferLoader {
    abstract var ints: IntArray
    override val type: GLPrimitive = GLPrimitive.short()

    companion object {
        fun createArrayBuffer(ints: IntArray): IntBufferLoader {
            return MechanicaLoader.bufferLoader.arrayBufferLoaders.ints(ints)
        }
    }
}