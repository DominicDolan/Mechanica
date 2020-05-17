package com.mechanica.engine.models

import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.resources.Resource

interface Image : Bindable {
    val id: Int

    companion object {
        operator fun invoke(id: Int) = GLLoader.graphicsLoader.loadImage(id)
        fun loadImage(res: Resource) = GLLoader.graphicsLoader.loadImage(res)
    }
}