package com.mechanica.engine.context

import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.context.loader.LwjglLoader
import com.mechanica.engine.display.DesktopWindow

class DesktopApplication : Application {
    override val surfaceContext: SurfaceContext = GLFWContext()
    override val glContext: OpenGLContext = GL40Context(this)

    override fun createLoader() = LwjglLoader()

    override fun initialize(mainWindow: DesktopWindow, callbacks: EventCallbacks) {
        GLContextOld.initialize(mainWindow)
        GLContextOld.setCallbacks(mainWindow, callbacks)
        ALContext.initialize()
    }

    override fun terminate() {
        GLContextOld.free()
        GLFWContextOld.terminate()
        ALContext.destroy()
    }

    override fun startFrame() {
        GLContextOld.startFrame()
    }
}