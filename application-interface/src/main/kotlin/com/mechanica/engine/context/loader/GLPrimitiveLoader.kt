package com.mechanica.engine.context.loader

import com.mechanica.engine.utils.GLPrimitive

interface GLPrimitiveLoader {
    val glFloat: GLPrimitive
    val glInt: GLPrimitive
    val glShort: GLPrimitive
}