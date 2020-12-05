package com.mechanica.engine.models

import com.mechanica.engine.context.loader.MechanicaLoader
import com.mechanica.engine.resources.Resource

interface Image : Bindable {
    val id: Int

    companion object {
        operator fun invoke(id: Int) = MechanicaLoader.graphicsLoader.loadImage(id)
        fun loadImage(res: Resource) = MechanicaLoader.graphicsLoader.loadImage(res)
    }
}