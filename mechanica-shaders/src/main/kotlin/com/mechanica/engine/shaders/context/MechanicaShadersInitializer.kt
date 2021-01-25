package com.mechanica.engine.shaders.context

object MechanicaShadersInitializer {
    private var _loader: ShaderLoader? = null
    internal val loader: ShaderLoader
        get() = _loader ?: throw UninitializedPropertyAccessException("The OpenGL context has not been initialized")

    fun initialize(loader: ShaderLoader) {
        if (_loader == null) {
            _loader = loader
        } else throw IllegalStateException("The OpenGl Shaders context has already been initialized")
    }
}