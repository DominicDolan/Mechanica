package com.mechanica.engine.context.loader

import com.mechanica.engine.models.Image
import com.mechanica.engine.models.Model

interface GraphicsLoader {

    fun loadImage(id: Int): Image

    fun drawArrays(model: Model)
    fun drawElements(model: Model)

}