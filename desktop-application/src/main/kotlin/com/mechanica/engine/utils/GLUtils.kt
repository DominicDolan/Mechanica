package com.mechanica.engine.utils

import com.mechanica.engine.resources.Resource
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL40
import org.lwjgl.stb.STBImage
import java.nio.ByteBuffer


fun enableAlphaBlending() {
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
}

class ImageData(val buffer: ByteBuffer) {
    val data: ByteBuffer?
    val width: Int
    val height: Int

    constructor(resource: Resource) : this(resource.buffer)

    init {
        val widthBuffer = BufferUtils.createIntBuffer(1)
        val heightBuffer = BufferUtils.createIntBuffer(1)
        val componentsBuffer = BufferUtils.createIntBuffer(1)

        data = STBImage.stbi_load_from_memory(buffer, widthBuffer, heightBuffer, componentsBuffer, 4)
        width = widthBuffer.get()
        height = heightBuffer.get()
    }

    fun free() {
        if (data != null) {
            STBImage.stbi_image_free(data)
        }
    }
}

/*
    GL_BYTE           = 0x1400,
    GL_UNSIGNED_BYTE  = 0x1401,
    GL_SHORT          = 0x1402,
    GL_UNSIGNED_SHORT = 0x1403,
    GL_INT            = 0x1404,
    GL_UNSIGNED_INT   = 0x1405,
    GL_FLOAT          = 0x1406,
    GL_2_BYTES        = 0x1407,
    GL_3_BYTES        = 0x1408,
    GL_4_BYTES        = 0x1409,
    GL_DOUBLE         = 0x140A;

     */

inline fun <reified T : Number> getGLType(): Int {
    return when {
        0.toByte() is T -> GL40.GL_BYTE
        0.toShort() is T -> GL40.GL_SHORT
        0 is T -> GL40.GL_INT
        0f is T -> GL40.GL_FLOAT
        0.0 is T -> GL40.GL_DOUBLE
        else -> throw UnsupportedOperationException("Only types of Byte, Short, Int, Float, Double are supported")
    }
}

fun getGLType(type: Number): Int {
    return when(type) {
        is Byte -> GL40.GL_BYTE
        is Short -> GL40.GL_SHORT
        is Int -> GL40.GL_INT
        is Float -> GL40.GL_FLOAT
        is Double -> GL40.GL_DOUBLE
        else -> throw UnsupportedOperationException("Only types of Byte, Short, Int, Float, Double are supported")
    }
}

fun getByteCount(type: Number): Int {
    return when(type) {
        is Byte -> 1
        is Short -> 2
        is Int -> 4
        is Float -> 4
        is Double -> 8
        is Long -> 8
        else -> -1
    }
}
