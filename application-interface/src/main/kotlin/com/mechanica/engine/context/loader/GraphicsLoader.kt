package com.mechanica.engine.context.loader

import com.mechanica.engine.models.Image
import com.mechanica.engine.models.Model
import com.mechanica.engine.resources.Resource

interface GraphicsLoader {

    val glDrawer: GLDrawerLoader

    fun loadImage(id: Int): Image
    fun loadImage(res: Resource): Image

    fun drawArrays(model: Model)
    fun drawElements(model: Model)

}