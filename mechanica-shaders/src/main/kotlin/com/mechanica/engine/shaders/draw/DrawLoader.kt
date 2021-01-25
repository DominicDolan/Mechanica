package com.mechanica.engine.shaders.draw

interface DrawLoader {

    val glTrianglesDrawer: GLDrawerLoader

    fun clearColor(r: Float, g: Float, b: Float, a: Float)

    val glTriangleFanDrawer: GLDrawerLoader
    val glTriangleStripDrawer: GLDrawerLoader
    val glPointDrawer: GLDrawerLoader
    val glLineLoopDrawer: GLDrawerLoader
    val glLinesDrawer: GLDrawerLoader
    val glLineStripDrawer: GLDrawerLoader
}