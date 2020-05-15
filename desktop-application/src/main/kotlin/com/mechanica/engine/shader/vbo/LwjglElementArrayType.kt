package com.mechanica.engine.shader.vbo

import com.mechanica.engine.vertices.ElementArrayType

class LwjglElementArrayType : ElementArrayType {
    override fun createBuffer(array: ShortArray) = LwjglIndexArray(array)
}