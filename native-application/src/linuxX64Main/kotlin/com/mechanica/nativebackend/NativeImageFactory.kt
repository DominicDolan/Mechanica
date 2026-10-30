@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.shaders.context.ImageDimensions
import com.mechanica.engine.shaders.context.ImageFactory
import com.mechanica.engine.shaders.models.Image
import com.mechanica.nativebackend.stb.*
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*

class NativeImageFactory : ImageFactory {
    override fun dimensions(encoded: ByteArray): ImageDimensions = memScoped {
        require(encoded.isNotEmpty()) { "Empty image" }
        val width = alloc<IntVar>()
        val height = alloc<IntVar>()
        val channels = alloc<IntVar>()
        val valid = encoded.usePinned {
            stbi_info_from_memory(it.addressOf(0).reinterpret(), encoded.size, width.ptr, height.ptr, channels.ptr)
        }
        require(valid != 0) { "Invalid image: ${stbi_failure_reason()?.toKString()}" }
        ImageDimensions(width.value, height.value)
    }

    override fun bindToUnit(image: Image, unit: Int) {
        require(unit >= 0)
        glActiveTexture(GL_TEXTURE0.toUInt() + unit.toUInt())
        try { image.bind() } finally { glActiveTexture(GL_TEXTURE0.toUInt()) }
    }

    override fun loadImage(id: Int): Image = NativeImage(id)

    override fun loadImage(encoded: ByteArray): Image = memScoped {
        require(encoded.isNotEmpty()) { "Empty image" }
        val width = alloc<IntVar>()
        val height = alloc<IntVar>()
        val channels = alloc<IntVar>()
        val pixels = encoded.usePinned {
            stbi_load_from_memory(it.addressOf(0).reinterpret(), encoded.size, width.ptr, height.ptr, channels.ptr, 4)
        } ?: error("Unable to decode image: ${stbi_failure_reason()?.toKString()}")
        try {
            val id = alloc<UIntVar>()
            glGenTextures(1, id.ptr)
            check(id.value != 0u) { "Unable to create texture" }
            glBindTexture(GL_TEXTURE_2D.toUInt(), id.value)
            val alignment = alloc<IntVar>()
            glGetIntegerv(GL_UNPACK_ALIGNMENT.toUInt(), alignment.ptr)
            try {
                glPixelStorei(GL_UNPACK_ALIGNMENT.toUInt(), 1)
                glTexImage2D(GL_TEXTURE_2D.toUInt(), 0, GL_RGBA, width.value, height.value, 0, GL_RGBA.toUInt(), GL_UNSIGNED_BYTE.toUInt(), pixels)
                val levels = minOf(4, 31 - maxOf(width.value, height.value).countLeadingZeroBits())
                glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_MAX_LEVEL.toUInt(), levels)
                glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_MAG_FILTER.toUInt(), GL_LINEAR)
                glTexParameteri(GL_TEXTURE_2D.toUInt(), GL_TEXTURE_MIN_FILTER.toUInt(), if (levels == 0) GL_LINEAR else GL_LINEAR_MIPMAP_LINEAR)
                if (levels > 0) glGenerateMipmap(GL_TEXTURE_2D.toUInt())
            } finally { glPixelStorei(GL_UNPACK_ALIGNMENT.toUInt(), alignment.value) }
            NativeImage(id.value.toInt())
        } finally { stbi_image_free(pixels) }
    }
}

private class NativeImage(override val id: Int) : Image {
    override fun bind() = glBindTexture(GL_TEXTURE_2D.toUInt(), id.toUInt())
}
