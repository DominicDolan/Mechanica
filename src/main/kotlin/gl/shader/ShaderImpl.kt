package gl.shader

import gl.script.ShaderScript

internal class ShaderImpl(
        override val vertex: ShaderScript,
        override val fragment: ShaderScript,
        override val tessellation: ShaderScript?,
        override val geometry: ShaderScript?): Shader {

    private var _loader: ShaderLoader? = null
    private val loader: ShaderLoader
        get() {
            val l = this._loader
            val loader = l ?: ShaderLoader(vertex, fragment, tessellation, geometry)
            this._loader = loader
            return loader
        }
    override val id: Int
        get() = loader.id

}