package com.mechanica.engine.context.loader

import com.mechanica.engine.resources.Resource
import com.mechanica.engine.shader.LwjglImage
import org.lwjgl.opengl.GL42

class LwjglGraphicsLoader : GraphicsLoader {
    override val glDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_TRIANGLES, GL42.GL_UNSIGNED_SHORT)
    override val glPointDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_POINTS, GL42.GL_UNSIGNED_SHORT)
    override val glLineLoopDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_LINE_LOOP, GL42.GL_UNSIGNED_SHORT)
    override val glLinesDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_LINES, GL42.GL_UNSIGNED_SHORT)
    override val glLineStripDrawer: GLDrawerLoader = LwjglDrawerLoader(GL42.GL_LINE_STRIP, GL42.GL_UNSIGNED_SHORT)

    override fun loadImage(id: Int) = LwjglImage(id)
    override fun loadImage(res: Resource) = com.mechanica.engine.utils.loadImage(res)

}