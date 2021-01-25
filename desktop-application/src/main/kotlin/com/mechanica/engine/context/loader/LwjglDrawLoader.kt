package com.mechanica.engine.context.loader

import com.mechanica.engine.shaders.draw.DrawLoader
import com.mechanica.engine.shaders.draw.GLDrawerLoader
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL42

class LwjglDrawLoader : DrawLoader {
    override val glTrianglesDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_TRIANGLES, GL42.GL_UNSIGNED_SHORT)
    override val glPointDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_POINTS, GL42.GL_UNSIGNED_SHORT)
    override val glTriangleFanDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_TRIANGLE_FAN, GL42.GL_UNSIGNED_SHORT)
    override val glTriangleStripDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_TRIANGLE_STRIP, GL42.GL_UNSIGNED_SHORT)
    override val glLineLoopDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_LINE_LOOP, GL42.GL_UNSIGNED_SHORT)
    override val glLinesDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_LINES, GL42.GL_UNSIGNED_SHORT)
    override val glLineStripDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_LINE_STRIP, GL42.GL_UNSIGNED_SHORT)

    override fun clearColor(r: Float, g: Float, b: Float, a: Float) {
        GL11.glClearColor(r, g, b, a)
    }
}