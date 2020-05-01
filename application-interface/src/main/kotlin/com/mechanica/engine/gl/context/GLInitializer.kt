package com.mechanica.engine.gl.context

object GLInitializer {
    private var _context: GLGeneric? = null
    internal val context: GLGeneric
        get() = _context ?: throw UninitializedPropertyAccessException("The OpenGL context has not been initialized")

    fun initialize(context: GLGeneric) {
        if (_context == null) {
            _context = context
        } else throw IllegalStateException("The OpenGl context has already been initialized")
    }
}