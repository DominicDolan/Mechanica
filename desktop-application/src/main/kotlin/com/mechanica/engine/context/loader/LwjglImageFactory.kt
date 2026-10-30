package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.LwjglImage
import com.mechanica.engine.shaders.context.ImageFactory
import com.mechanica.engine.utils.toDirectBuffer
import com.mechanica.engine.shaders.context.ImageDimensions
import com.mechanica.engine.shaders.models.Image
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL13
import org.lwjgl.stb.STBImage

class LwjglImageFactory: ImageFactory {
    override fun dimensions(encoded: ByteArray): ImageDimensions {
        val data = encoded.toDirectBuffer()
        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val components = BufferUtils.createIntBuffer(1)
        require(STBImage.stbi_info_from_memory(data, width, height, components)) {
            "Invalid image: ${STBImage.stbi_failure_reason()}"
        }
        return ImageDimensions(width[0], height[0])
    }

    override fun bindToUnit(image: Image, unit: Int) {
        require(unit >= 0)
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + unit)
        try { image.bind() } finally { GL13.glActiveTexture(GL13.GL_TEXTURE0) }
    }

    override fun loadImage(id: Int) = LwjglImage(id)

    override fun loadImage(encoded: ByteArray) = LwjglImage.create(encoded.toDirectBuffer())
}