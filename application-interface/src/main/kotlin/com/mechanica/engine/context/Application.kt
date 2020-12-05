package com.mechanica.engine.context

import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.context.loader.MechanicaLoader
import com.mechanica.engine.display.DesktopWindow

interface Application {
    val surfaceContext: SurfaceContext
    val glContext: OpenGLContext

    fun createLoader(): MechanicaLoader

    fun initialize(mainWindow: DesktopWindow, callbacks: EventCallbacks)
    fun terminate()

    fun startFrame()

}