package com.mechanica.engine.context

import com.mechanica.engine.context.loader.MechanicaFactory

interface Application {
    val surfaceContext: SurfaceContext
    val glContext: OpenGLContext
    val audioContext: AudioContext

    fun createFactory(): MechanicaFactory

    fun terminate() {
        surfaceContext.destroy()
        glContext.destroy()
        audioContext.destroy()
    }

}