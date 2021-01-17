package com.mechanica.engine.context

import com.mechanica.engine.context.loader.MechanicaLoader

interface Application {
    val surfaceContext: SurfaceContext
    val glContext: OpenGLContext
    val audioContext: AudioContext

    fun createLoader(): MechanicaLoader

    fun terminate() {
        surfaceContext.destroy()
        glContext.destroy()
        audioContext.destroy()
    }

}