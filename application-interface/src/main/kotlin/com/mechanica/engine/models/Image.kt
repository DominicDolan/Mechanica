package com.mechanica.engine.models

import com.mechanica.engine.context.loader.GLLoader

interface Image : Bindable {
    val id: Int

    companion object {
        operator fun invoke(id: Int) = GLLoader.graphicsLoader.loadImage(id)
    }
}