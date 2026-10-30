@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.context.loader.*
import com.mechanica.samples.nativegl.*

class NativeScissorFactory : ScissorFactory {
    override fun enableScissor(x: Int, y: Int, width: Int, height: Int) {
        glEnable(GL_SCISSOR_TEST.toUInt())
        glScissor(x, y, maxOf(0, width), maxOf(0, height))
    }
    override fun disableScissor() = glDisable(GL_SCISSOR_TEST.toUInt())
}

class NativeStencilFactory : StencilFactory {
    override fun enableStencilWrite(write: Boolean) = enableStencilWrite(if (write) 0xff else 0)
    override fun enableStencilWrite(allowedBits: Int) {
        glEnable(GL_STENCIL_TEST.toUInt())
        glStencilMask(allowedBits.toUInt())
    }
    override fun prepareStencil(type: StencilType, ref: Int, mask: Int) {
        glEnable(GL_STENCIL_TEST.toUInt())
        val func = when (type) {
            StencilType.Union -> GL_ALWAYS
            StencilType.Difference -> GL_NOTEQUAL
            StencilType.Intersection -> GL_EQUAL
        }
        glStencilFunc(func.toUInt(), ref, mask.toUInt())
        glStencilOp(GL_KEEP.toUInt(), GL_KEEP.toUInt(), GL_REPLACE.toUInt())
    }
    override fun disableStencilTest() {
        glStencilMask(0u)
        glStencilFunc(GL_ALWAYS.toUInt(), 0, 0xffu)
        glDisable(GL_STENCIL_TEST.toUInt())
    }
}

class NativeConstants : GLConstants {
    override val vboTypes = object : GLConstants.VBOTypes {
        override val arrayBuffer = GL_ARRAY_BUFFER
        override val elementArrayBuffer = GL_ELEMENT_ARRAY_BUFFER
    }
    override val glPrimitiveTypes = object : GLConstants.GLPrimitiveTypes {
        override val byte = GL_BYTE
        override val short = GL_SHORT
        override val int = GL_INT
        override val float = GL_FLOAT
        override val double = GL_DOUBLE
    }
}
