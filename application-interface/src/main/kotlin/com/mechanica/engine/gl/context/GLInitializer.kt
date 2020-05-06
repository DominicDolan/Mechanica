package com.mechanica.engine.gl.context

import com.mechanica.engine.gl.context.loader.GLLoader

object GLInitializer {
    private var _loader: GLLoader? = null
    internal val loader: GLLoader
        get() = _loader ?: throw UninitializedPropertyAccessException("The OpenGL context has not been initialized")

    fun initialize(loader: GLLoader) {
        if (_loader == null) {
            _loader = loader
        } else throw IllegalStateException("The OpenGl context has already been initialized")
    }
}