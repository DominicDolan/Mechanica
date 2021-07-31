package com.mechanica.engine.shaders.glPrimitives

import com.mechanica.engine.shaders.context.ShaderFactory

interface GLPrimitive {
    val id: Int
    val byteSize: Int

    companion object {
        fun float() = ShaderFactory.glPrimitives.glFloat
        fun int() = ShaderFactory.glPrimitives.glInt
        fun short() = ShaderFactory.glPrimitives.glShort
    }
}

interface GLPrimitiveFactory {
    val glFloat: GLPrimitive
    val glInt: GLPrimitive
    val glShort: GLPrimitive
}