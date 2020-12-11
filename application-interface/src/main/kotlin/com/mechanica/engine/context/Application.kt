package com.mechanica.engine.context

import com.mechanica.engine.context.loader.MechanicaLoader

interface Application {
    val surfaceContext: SurfaceContext
    val glContext: OpenGLContext

    fun createLoader(): MechanicaLoader

    fun terminate()

}