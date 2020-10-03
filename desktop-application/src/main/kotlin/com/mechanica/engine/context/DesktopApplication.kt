package com.mechanica.engine.context

import com.mechanica.engine.context.loader.LwjglLoader
import com.mechanica.engine.display.Window
import org.lwjgl.glfw.GLFW

class DesktopApplication : Application {
    override fun initialize(mainWindow: Window) {
        GLContext.initialize(mainWindow)
        val callbacks = GLInitializer.initialize(LwjglLoader())
        GLContext.setCallbacks(mainWindow, callbacks)
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

    override fun activateContext(window: Window?) {
        val windowId = window?.id ?: 0L
        if (GLFW.glfwGetCurrentContext() != windowId) {
            GLContext.initContext(windowId)
        }
    }
}