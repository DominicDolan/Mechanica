package com.mechanica.engine.context.loader

import com.mechanica.engine.context.loader.BufferLoader
import org.lwjgl.BufferUtils
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer

class LwjglBufferLoader : BufferLoader {
    override fun byteBuffer(size: Int) = BufferUtils.createByteBuffer(size)

    override fun intBuffer(size: Int) = BufferUtils.createIntBuffer(size)

    override fun floatBuffer(size: Int) = BufferUtils.createFloatBuffer(size)

    override fun useByteBuffer(size: Int, block: ByteBuffer.() -> Unit) {
        val buffer = MemoryUtil.memAlloc(size)
        block(buffer)
        MemoryUtil.memFree(buffer)
    }
}
