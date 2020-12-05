package com.mechanica.engine.context.loader

interface ShaderFunctions {
    val id: Int
    val glslHeader: String
    fun useShader()
    fun loadUniformLocation(name: String): Int
    fun loadAttributeLocation(name: String): Int
}