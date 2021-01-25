package com.mechanica.engine.shaders.models

import com.mechanica.engine.shaders.context.ShaderLoader
import java.nio.ByteBuffer

interface Image : Bindable {
    val id: Int

    companion object {
        operator fun invoke(id: Int) = create(id)

        fun create(id: Int) = ShaderLoader.imageLoader.loadImage(id)
        fun create(buffer: ByteBuffer) = ShaderLoader.imageLoader.loadImage(buffer)
    }
}