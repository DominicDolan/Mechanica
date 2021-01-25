package com.mechanica.engine.shader

import com.mechanica.engine.resources.Resource
import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.utils.ImageData
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL30
import java.nio.ByteBuffer

class LwjglImage(override val id: Int) : Image {

    override fun bind() {
        glBindTexture(GL11.GL_TEXTURE_2D, id)
    }

    companion object {
        fun create(resource: Resource): LwjglImage {
            return create(resource.buffer)
        }

        fun create(buffer: ByteBuffer): LwjglImage {
            val data = ImageData(buffer)

            var image = LwjglImage(0)
            if (data.data != null) {
                image = create(data.data, data.width, data.height)
            }
            data.free()

            return image
        }


        fun create(buffer: ByteBuffer, width: Int, height: Int, levels: Int = 4, format: Int = GL11.GL_RGBA): LwjglImage {

            val image = GL11.glGenTextures()
            glBindTexture(GL11.GL_TEXTURE_2D, image)

            setMipmapping(buffer, width, height, levels, format)

            check(GL11.glIsTexture(image)) { "Unable to load texture" }

            return LwjglImage(image)
        }

        private fun setMipmapping(data: ByteBuffer, width: Int, height: Int, levels: Int, format: Int) {
            for (i in 0..levels) {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i, format, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, data)
            }
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            GL11.glTexParameteri (GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, levels)

        }
    }
}