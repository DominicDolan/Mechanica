package com.mechanica.engine.context.loader

import com.mechanica.engine.context.loader.GLConstants
import org.lwjgl.opengl.GL40.*

class LwjglConstants : GLConstants {
    override val vboTypes = object : GLConstants.VBOTypes {
        override val arrayBuffer = GL_ARRAY_BUFFER
        override val elementArrayBuffer = GL_ELEMENT_ARRAY_BUFFER

    }

    /*
    GL_BYTE           = 0x1400,
    GL_UNSIGNED_BYTE  = 0x1401,
    GL_SHORT          = 0x1402,
    GL_UNSIGNED_SHORT = 0x1403,
    GL_INT            = 0x1404,
    GL_UNSIGNED_INT   = 0x1405,
    GL_FLOAT          = 0x1406,
    GL_2_BYTES        = 0x1407,
    GL_3_BYTES        = 0x1408,
    GL_4_BYTES        = 0x1409,
    GL_DOUBLE         = 0x140A;

     */
    override val glPrimitiveTypes = object : GLConstants.GLPrimitiveTypes {
        override val byte = GL_BYTE
        override val short = GL_SHORT
        override val int = GL_INT
        override val float = GL_FLOAT
        override val double = GL_DOUBLE
    }
}