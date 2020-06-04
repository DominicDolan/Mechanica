package com.mechanica.engine.context

import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.context.loader.GLLoader

object GLInitializer {
    private var _loader: GLLoader? = null
    internal val loader: GLLoader
        get() = _loader ?: throw UninitializedPropertyAccessException("The OpenGL context has not been initialized")

    fun initialize(loader: GLLoader): EventCallbacks {
        if (_loader == null) {
            _loader = loader
            return EventCallbacks.create()
        } else throw IllegalStateException("The OpenGl context has already been initialized")
    }
}