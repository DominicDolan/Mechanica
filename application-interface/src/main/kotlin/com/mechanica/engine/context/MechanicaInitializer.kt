package com.mechanica.engine.context

import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.context.loader.MechanicaLoader

object MechanicaInitializer {
    private var _loader: MechanicaLoader? = null
    internal val loader: MechanicaLoader
        get() = _loader ?: throw UninitializedPropertyAccessException("The OpenGL context has not been initialized")

    fun initialize(loader: MechanicaLoader): EventCallbacks {
        if (_loader == null) {
            _loader = loader
            return EventCallbacks.create()
        } else throw IllegalStateException("The OpenGl context has already been initialized")
    }
}