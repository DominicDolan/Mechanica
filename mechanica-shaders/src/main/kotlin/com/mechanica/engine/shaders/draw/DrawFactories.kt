package com.mechanica.engine.shaders.draw

interface DrawFactories {

    val glTrianglesDrawer: GLDrawerFactory

    fun clearColor(r: Float, g: Float, b: Float, a: Float)

    val glTriangleFanDrawer: GLDrawerFactory
    val glTriangleStripDrawer: GLDrawerFactory
    val glPointDrawer: GLDrawerFactory
    val glLineLoopDrawer: GLDrawerFactory
    val glLinesDrawer: GLDrawerFactory
    val glLineStripDrawer: GLDrawerFactory
}