package com.mechanica.engine.utils

import com.mechanica.engine.context.loader.MechanicaLoader
import com.mechanica.engine.models.Bindable


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
            return MechanicaLoader.bufferLoader.arrayBufferFactory.floats(floats)
        }
    }
}

abstract class ShortBufferObject : GLBufferObject {
    abstract var shorts: ShortArray
    override val type: GLPrimitive = GLPrimitive.short()

    companion object {
        fun createArrayBuffer(shorts: ShortArray): ShortBufferObject {
            return MechanicaLoader.bufferLoader.arrayBufferFactory.shorts(shorts)
        }

        fun createElementArrayBuffer(shorts: ShortArray): ShortBufferObject {
            return MechanicaLoader.bufferLoader.elementArrayBufferFactory.shorts(shorts)
        }
    }
}

abstract class IntBufferObject : GLBufferObject {
    abstract var ints: IntArray
    override val type: GLPrimitive = GLPrimitive.short()

    companion object {
        fun createArrayBuffer(ints: IntArray): IntBufferObject {
            return MechanicaLoader.bufferLoader.arrayBufferFactory.ints(ints)
        }
    }
}