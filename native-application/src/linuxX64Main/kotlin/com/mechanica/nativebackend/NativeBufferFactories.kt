@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.shaders.buffers.*
import com.mechanica.engine.shaders.context.GLBufferFactory
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*

class NativeBufferFactories : BufferFactories {
    override val arrayBufferFactory = NativeBufferFactory(GL_ARRAY_BUFFER.toUInt())
    override val elementArrayBufferFactory = NativeBufferFactory(GL_ELEMENT_ARRAY_BUFFER.toUInt())
    override val copyReadBufferFactory = NativeBufferFactory(GL_COPY_READ_BUFFER.toUInt())
    override val copyWriteBufferFactory = NativeBufferFactory(GL_COPY_WRITE_BUFFER.toUInt())
}

class NativeBufferFactory(private val target: UInt) : GLBufferFactory {
    override fun floats(floats: FloatArray): FloatBufferObject = NativeFloatBuffer(floats, target)
    override fun shorts(shorts: ShortArray): ShortBufferObject = NativeShortBuffer(shorts, target)
    override fun ints(ints: IntArray): IntBufferObject = NativeIntBuffer(ints, target)
}

private fun newBuffer(): UInt = memScoped {
    val id = alloc<UIntVar>()
    glGenBuffers(1, id.ptr)
    check(id.value != 0u) { "Unable to create OpenGL buffer" }
    id.value
}
private fun bufferSize(target: UInt): Long = memScoped {
    val size = alloc<IntVar>()
    glGetBufferParameteriv(target, GL_BUFFER_SIZE.toUInt(), size.ptr)
    size.value.toLong()
}

private class NativeFloatBuffer(override var floats: FloatArray, private val target: UInt) : FloatBufferObject() {
    override val id = newBuffer().toInt()
    init { storeData(floats.size.toLong()) }
    override fun bind() = glBindBuffer(target, id.toUInt())
    override fun initiateBuffer(byteCapacity: Long) = glBufferData(target, byteCapacity, null, GL_STATIC_DRAW.toUInt())
    override fun getExistingBufferSize(): Long { bind(); return bufferSize(target) }
    override fun storeSubData(offset: Long) {
        if (floats.isNotEmpty()) floats.usePinned { glBufferSubData(target, offset, floats.size.toLong() * 4, it.addressOf(0)) }
    }
}
private class NativeShortBuffer(override var shorts: ShortArray, private val target: UInt) : ShortBufferObject() {
    override val id = newBuffer().toInt()
    init { storeData(shorts.size.toLong()) }
    override fun bind() = glBindBuffer(target, id.toUInt())
    override fun initiateBuffer(byteCapacity: Long) = glBufferData(target, byteCapacity, null, GL_STATIC_DRAW.toUInt())
    override fun getExistingBufferSize(): Long { bind(); return bufferSize(target) }
    override fun storeSubData(offset: Long) {
        if (shorts.isNotEmpty()) shorts.usePinned { glBufferSubData(target, offset, shorts.size.toLong() * 2, it.addressOf(0)) }
    }
}
private class NativeIntBuffer(override var ints: IntArray, private val target: UInt) : IntBufferObject() {
    override val id = newBuffer().toInt()
    init { storeData(ints.size.toLong()) }
    override fun bind() = glBindBuffer(target, id.toUInt())
    override fun initiateBuffer(byteCapacity: Long) = glBufferData(target, byteCapacity, null, GL_STATIC_DRAW.toUInt())
    override fun getExistingBufferSize(): Long { bind(); return bufferSize(target) }
    override fun storeSubData(offset: Long) {
        if (ints.isNotEmpty()) ints.usePinned { glBufferSubData(target, offset, ints.size.toLong() * 4, it.addressOf(0)) }
    }
}
