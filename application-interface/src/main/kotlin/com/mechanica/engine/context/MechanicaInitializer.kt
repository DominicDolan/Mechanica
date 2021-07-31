package com.mechanica.engine.context

import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.context.loader.MechanicaFactory
import com.mechanica.engine.shaders.context.MechanicaShadersInitializer

object MechanicaInitializer {
    private var _factory: MechanicaFactory? = null
    internal val factory: MechanicaFactory
        get() = _factory ?: throw UninitializedPropertyAccessException("The OpenGL context has not been initialized")

    fun initialize(factory: MechanicaFactory): EventCallbacks {
        if (_factory == null) {
            _factory = factory
            MechanicaShadersInitializer.initialize(factory.shaderFactory)
            return EventCallbacks.create()
        } else throw IllegalStateException("The OpenGl context has already been initialized")
    }
}