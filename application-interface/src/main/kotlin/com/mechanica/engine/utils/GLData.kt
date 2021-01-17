package com.mechanica.engine.utils

import com.mechanica.engine.context.loader.MechanicaLoader

interface GLPrimitive {
    val id: Int
    val byteSize: Int

    companion object {
        fun float() = MechanicaLoader.glPrimitives.glFloat
        fun int() = MechanicaLoader.glPrimitives.glInt
        fun short() = MechanicaLoader.glPrimitives.glShort
    }
}