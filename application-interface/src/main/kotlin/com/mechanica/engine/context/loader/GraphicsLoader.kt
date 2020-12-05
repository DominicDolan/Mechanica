package com.mechanica.engine.context.loader

import com.mechanica.engine.models.Image
import com.mechanica.engine.resources.Resource

interface GraphicsLoader {

    val glDrawer: GLDrawerLoader

    fun loadImage(id: Int): Image
    fun loadImage(res: Resource): Image

    fun clearColor(r: Float, g: Float, b: Float, a: Float)

    val glTriangleFanDrawer: GLDrawerLoader
    val glTriangleStripDrawer: GLDrawerLoader
    val glPointDrawer: GLDrawerLoader
    val glLineLoopDrawer: GLDrawerLoader
    val glLinesDrawer: GLDrawerLoader
    val glLineStripDrawer: GLDrawerLoader
}