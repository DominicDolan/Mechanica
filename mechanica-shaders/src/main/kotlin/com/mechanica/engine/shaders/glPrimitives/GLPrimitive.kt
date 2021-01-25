package com.mechanica.engine.shaders.glPrimitives

import com.mechanica.engine.shaders.context.ShaderLoader

interface GLPrimitive {
    val id: Int
    val byteSize: Int

    companion object {
        fun float() = ShaderLoader.glPrimitives.glFloat
        fun int() = ShaderLoader.glPrimitives.glInt
        fun short() = ShaderLoader.glPrimitives.glShort
    }
}

interface GLPrimitiveLoader {
    val glFloat: GLPrimitive
    val glInt: GLPrimitive
    val glShort: GLPrimitive
}