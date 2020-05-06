package com.mechanica.engine.gl.context.loader

interface GLConstants {

    val vboTypes: VBOTypes
    interface VBOTypes {
        val arrayBuffer: Int
        val elementArrayBuffer: Int
    }

    val glPrimitiveTypes: GLPrimitiveTypes
    interface GLPrimitiveTypes {
        val byte: Int
        val short: Int
        val int: Int
        val float: Int
        val double: Int
    }
}