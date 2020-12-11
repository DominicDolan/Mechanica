package com.mechanica.engine.context.loader

import com.mechanica.engine.utils.FloatBufferLoader
import com.mechanica.engine.utils.IntBufferLoader
import com.mechanica.engine.utils.ShortBufferLoader
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL40
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class LwjglBufferLoader : BufferLoader {
    override fun byteBuffer(size: Int): ByteBuffer = BufferUtils.createByteBuffer(size)

    override fun intBuffer(size: Int): IntBuffer = BufferUtils.createIntBuffer(size)

    override fun floatBuffer(size: Int): FloatBuffer = BufferUtils.createFloatBuffer(size)

    override fun useByteBuffer(size: Int, block: ByteBuffer.() -> Unit) {
        val buffer = MemoryUtil.memAlloc(size)
        block(buffer)
        MemoryUtil.memFree(buffer)
    }

    override val arrayBufferLoaders: GLBufferLoaders = LwjglBufferLoaders(GL40.GL_ARRAY_BUFFER)
    override val elementArrayBufferLoaders: GLBufferLoaders = LwjglBufferLoaders(GL40.GL_ELEMENT_ARRAY_BUFFER)
    override val copyReadBufferLoaders: GLBufferLoaders = LwjglBufferLoaders(GL40.GL_COPY_READ_BUFFER)
    override val copyWriteBufferLoaders: GLBufferLoaders = LwjglBufferLoaders(GL40.GL_COPY_WRITE_BUFFER)

}


class LwjglBufferLoaders(private val bufferTarget: Int) : GLBufferLoaders {
    override fun floats(floats: FloatArray): FloatBufferLoader {
        return LwjglFloatBufferLoader(floats, bufferTarget)
    }

    override fun shorts(shorts: ShortArray): ShortBufferLoader {
        return LwjglShortBufferLoader(shorts, bufferTarget)
    }

    override fun ints(ints: IntArray): IntBufferLoader {
        return LwjglIntBufferLoader(ints, bufferTarget)
    }

}

class LwjglFloatBufferLoader(override var floats: FloatArray, private val bufferTarget: Int) : FloatBufferLoader() {
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

class LwjglShortBufferLoader(override var shorts: ShortArray, private val bufferTarget: Int) : ShortBufferLoader() {
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

class LwjglIntBufferLoader(override var ints: IntArray, private val bufferTarget: Int) : IntBufferLoader() {
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

