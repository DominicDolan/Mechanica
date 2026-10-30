package com.mechanica.engine.shaders.models

import com.mechanica.engine.shaders.context.ShaderFactory

interface Image : Bindable {
    val id: Int

    companion object {
        operator fun invoke(id: Int) = create(id)

        fun create(id: Int) = ShaderFactory.imageFactory.loadImage(id)
        fun create(encoded: ByteArray) = ShaderFactory.imageFactory.loadImage(encoded)
    }
}