package com.mechanica.engine.shaders.context

object MechanicaShadersInitializer {
    private var _factory: ShaderFactory? = null
    internal val factory: ShaderFactory
        get() = _factory ?: throw UninitializedPropertyAccessException("The OpenGL context has not been initialized")

    fun initialize(factory: ShaderFactory) {
        if (_factory == null) {
            _factory = factory
        } else throw IllegalStateException("The OpenGl Shaders context has already been initialized")
    }
}