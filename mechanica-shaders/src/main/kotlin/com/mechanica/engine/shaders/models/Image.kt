package com.mechanica.engine.shaders.models

import com.mechanica.engine.shaders.context.ShaderFactory
import java.nio.ByteBuffer

interface Image : Bindable {
    val id: Int

    companion object {
        operator fun invoke(id: Int) = create(id)

        fun create(id: Int) = ShaderFactory.imageFactory.loadImage(id)
        fun create(buffer: ByteBuffer) = ShaderFactory.imageFactory.loadImage(buffer)
    }
}