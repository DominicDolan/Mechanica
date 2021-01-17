package com.mechanica.engine.context.loader

import com.mechanica.engine.utils.GLPrimitive
import org.lwjgl.opengl.GL11

class LwjglPrimitiveLoader : GLPrimitiveLoader {
    override val glFloat = object : GLPrimitive {
        override val id: Int = GL11.GL_FLOAT
        override val byteSize: Int = 4
    }
    override val glInt = object : GLPrimitive {
        override val id: Int = GL11.GL_INT
        override val byteSize: Int = 4
    }
    override val glShort = object : GLPrimitive {
        override val id: Int = GL11.GL_SHORT
        override val byteSize: Int = 2
    }
}