package com.mechanica.engine.context.loader

import com.mechanica.engine.shaders.draw.DrawFactories
import com.mechanica.engine.shaders.draw.GLDrawerFactory
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL42

class LwjglDrawFactories : DrawFactories {
    override val glTrianglesDrawer: GLDrawerFactory = LwjglDrawerFactory(GL42.GL_TRIANGLES, GL42.GL_UNSIGNED_SHORT)
    override val glPointDrawer: GLDrawerFactory = LwjglDrawerFactory(GL42.GL_POINTS, GL42.GL_UNSIGNED_SHORT)
    override val glTriangleFanDrawer: GLDrawerFactory = LwjglDrawerFactory(GL42.GL_TRIANGLE_FAN, GL42.GL_UNSIGNED_SHORT)
    override val glTriangleStripDrawer: GLDrawerFactory = LwjglDrawerFactory(GL42.GL_TRIANGLE_STRIP, GL42.GL_UNSIGNED_SHORT)
    override val glLineLoopDrawer: GLDrawerFactory = LwjglDrawerFactory(GL42.GL_LINE_LOOP, GL42.GL_UNSIGNED_SHORT)
    override val glLinesDrawer: GLDrawerFactory = LwjglDrawerFactory(GL42.GL_LINES, GL42.GL_UNSIGNED_SHORT)
    override val glLineStripDrawer: GLDrawerFactory = LwjglDrawerFactory(GL42.GL_LINE_STRIP, GL42.GL_UNSIGNED_SHORT)

    override fun clearColor(r: Float, g: Float, b: Float, a: Float) {
        GL11.glClearColor(r, g, b, a)
    }
}