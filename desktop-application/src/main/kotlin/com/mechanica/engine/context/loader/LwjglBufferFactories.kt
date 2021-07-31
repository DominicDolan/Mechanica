package com.mechanica.engine.context.loader

import com.mechanica.engine.shaders.buffers.BufferFactories
import com.mechanica.engine.shaders.buffers.FloatBufferObject
import com.mechanica.engine.shaders.buffers.IntBufferObject
import com.mechanica.engine.shaders.buffers.ShortBufferObject
import com.mechanica.engine.shaders.context.GLBufferFactory
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL40
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class LwjglBufferFactories : BufferFactories {
    override fun byteBuffer(size: Int): ByteBuffer = BufferUtils.createByteBuffer(size)

    override fun intBuffer(size: Int): IntBuffer = BufferUtils.createIntBuffer(size)

    override fun floatBuffer(size: Int): FloatBuffer = BufferUtils.createFloatBuffer(size)

    override fun useByteBuffer(size: Int, block: ByteBuffer.() -> Unit) {
        val buffer = MemoryUtil.memAlloc(size)
        block(buffer)
        MemoryUtil.memFree(buffer)
    }

    override val arrayBufferFactory: GLBufferFactory = LwjglBufferFactory(GL40.GL_ARRAY_BUFFER)
    override val elementArrayBufferFactory: GLBufferFactory = LwjglBufferFactory(GL40.GL_ELEMENT_ARRAY_BUFFER)
    override val copyReadBufferFactory: GLBufferFactory = LwjglBufferFactory(GL40.GL_COPY_READ_BUFFER)
    override val copyWriteBufferFactory: GLBufferFactory = LwjglBufferFactory(GL40.GL_COPY_WRITE_BUFFER)

}


class LwjglBufferFactory(private val bufferTarget: Int) : GLBufferFactory {
    override fun floats(floats: FloatArray): FloatBufferObject {
        return LwjglFloatBufferObject(floats, bufferTarget)
    }

    override fun shorts(shorts: ShortArray): ShortBufferObject {
        return LwjglShortBufferObject(shorts, bufferTarget)
    }

    override fun ints(ints: IntArray): IntBufferObject {
        return LwjglIntBufferObject(ints, bufferTarget)
    }

}

class LwjglFloatBufferObject(override var floats: FloatArray, private val bufferTarget: Int) : FloatBufferObject() {
    override val id: Int = GL15.glGenBuffers()

    init {
        storeData(floats.size.toLong())
    }

    override fun initiateBuffer(byteCapacity: Long) {
        GL40.glBufferData(bufferTarget, byteCapacity, GL40.GL_STATIC_DRAW)
    }

    override fun storeSubData(offset: Long) {
        GL15.glBufferSubData(bufferTarget, offset, floats)
    }

    override fun bind() {
        GL40.glBindBuffer(bufferTarget, id)
    }

    override fun getExistingBufferSize(): Long {
        bind()
        return GL15.glGetBufferParameteri(bufferTarget, GL15.GL_BUFFER_SIZE).toLong()
    }
}

class LwjglShortBufferObject(override var shorts: ShortArray, private val bufferTarget: Int) : ShortBufferObject() {
    override val id: Int = GL15.glGenBuffers()

    init {
        storeData(shorts.size.toLong())
    }

    override fun initiateBuffer(byteCapacity: Long) {
        GL40.glBufferData(bufferTarget, byteCapacity, GL40.GL_STATIC_DRAW)
    }

    override fun storeSubData(offset: Long) {
        GL15.glBufferSubData(bufferTarget, offset, shorts)
    }

    override fun bind() {
        GL40.glBindBuffer(bufferTarget, id)
    }

    override fun getExistingBufferSize(): Long {
        bind()
        return GL15.glGetBufferParameteri(bufferTarget, GL15.GL_BUFFER_SIZE).toLong()
    }
}

class LwjglIntBufferObject(override var ints: IntArray, private val bufferTarget: Int) : IntBufferObject() {
    override val id: Int = GL15.glGenBuffers()

    init {
        storeData(ints.size.toLong())
    }

    override fun initiateBuffer(byteCapacity: Long) {
        GL40.glBufferData(bufferTarget, byteCapacity, GL40.GL_STATIC_DRAW)
    }

    override fun storeSubData(offset: Long) {
        GL15.glBufferSubData(bufferTarget, offset, ints)
    }

    override fun bind() {
        GL40.glBindBuffer(bufferTarget, id)
    }

    override fun getExistingBufferSize(): Long {
        bind()
        return GL15.glGetBufferParameteri(bufferTarget, GL15.GL_BUFFER_SIZE).toLong()
    }
}

