package com.mechanica.engine.context.loader

import com.mechanica.engine.shader.LwjglImage
import com.mechanica.engine.shaders.context.ImageLoader
import java.nio.ByteBuffer

class LwjglImageLoader: ImageLoader {
    override fun loadImage(id: Int) = LwjglImage(id)

    override fun loadImage(buffer: ByteBuffer) = LwjglImage.create(buffer)
}