package com.mechanica.engine.shader.script

internal class ShaderImpl(
        override val vertex: ShaderScript,
        override val fragment: ShaderScript,
        override val tessellation: ShaderScript?,
        override val geometry: ShaderScript?): Shader() {

    private val loader: ShaderLoader by lazy { ShaderLoader(vertex, fragment, tessellation, geometry) }

    override val id: Int
        get() = loader.id

}