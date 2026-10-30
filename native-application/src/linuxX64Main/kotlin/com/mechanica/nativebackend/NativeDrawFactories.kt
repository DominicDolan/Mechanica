@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.shaders.draw.DrawFactories
import com.mechanica.engine.shaders.glPrimitives.GLPrimitive
import com.mechanica.engine.shaders.glPrimitives.GLPrimitiveFactory
import com.mechanica.samples.nativegl.*

class NativeDrawFactories : DrawFactories {
    private fun drawer(mode: Int) = NativeDrawerFactory(mode.toUInt(), GL_UNSIGNED_SHORT.toUInt())
    override val glTrianglesDrawer = drawer(GL_TRIANGLES)
    override val glPointDrawer = drawer(GL_POINTS)
    override val glTriangleFanDrawer = drawer(GL_TRIANGLE_FAN)
    override val glTriangleStripDrawer = drawer(GL_TRIANGLE_STRIP)
    override val glLineLoopDrawer = drawer(GL_LINE_LOOP)
    override val glLinesDrawer = drawer(GL_LINES)
    override val glLineStripDrawer = drawer(GL_LINE_STRIP)
    override fun clearColor(r: Float, g: Float, b: Float, a: Float) = glClearColor(r, g, b, a)
}

class NativePrimitiveFactory : GLPrimitiveFactory {
    private fun primitive(type: Int, size: Int) = object : GLPrimitive {
        override val id = type
        override val byteSize = size
    }
    override val glFloat = primitive(GL_FLOAT, 4)
    override val glInt = primitive(GL_INT, 4)
    override val glShort = primitive(GL_SHORT, 2)
}
