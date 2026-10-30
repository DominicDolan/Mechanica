package com.mechanica.engine.shader

import com.mechanica.engine.resources.Resource
import com.mechanica.engine.utils.toDirectBuffer
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
            return create(resource.readBytes().toDirectBuffer())
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


        fun create(buffer: ByteBuffer, width: Int, height: Int, levels: Int = 4, format: Int = GL11.GL_RGBA,
                   clampToEdge: Boolean = false): LwjglImage {

            val image = GL11.glGenTextures()
            glBindTexture(GL11.GL_TEXTURE_2D, image)

            setMipmapping(buffer, width, height, levels, format, clampToEdge)

            check(GL11.glIsTexture(image)) { "Unable to load texture" }

            return LwjglImage(image)
        }

        private fun setMipmapping(data: ByteBuffer, width: Int, height: Int, levels: Int, format: Int, clampToEdge: Boolean) {
            require(width > 0 && height > 0 && levels >= 0)
            val maxLevel = minOf(levels, 31 - Integer.numberOfLeadingZeros(maxOf(width, height)))
            // Single-channel font rows need not be multiples of OpenGL's default four-byte alignment.
            val alignment = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT)
            try {
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, format, width, height, 0, format, GL11.GL_UNSIGNED_BYTE, data)
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, maxLevel)
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                    if (maxLevel == 0) GL11.GL_LINEAR else GL11.GL_LINEAR_MIPMAP_LINEAR)
                if (clampToEdge) {
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
                }
                if (maxLevel > 0) GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            } finally {
                GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, alignment)
            }
        }
    }
}
