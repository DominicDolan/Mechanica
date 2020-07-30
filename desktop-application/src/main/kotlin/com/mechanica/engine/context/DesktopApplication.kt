package com.mechanica.engine.context

import com.mechanica.engine.context.loader.LwjglLoader
import com.mechanica.engine.display.Window

class DesktopApplication : Application {
    override fun initialize(window: Window) {
        GLContext.initialize(window)
        val callbacks = GLInitializer.initialize(LwjglLoader())
        GLContext.setCallbacks(window, callbacks)
        ALContext.initialize()
    }

    override fun terminate() {
        GLContext.free()
        GLFWContext.terminate()
        ALContext.destroy()
    }

    override fun startFrame() {
        GLContext.startFrame()
    }
}