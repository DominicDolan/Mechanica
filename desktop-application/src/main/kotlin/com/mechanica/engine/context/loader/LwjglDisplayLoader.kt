package com.mechanica.engine.context.loader

import com.mechanica.engine.display.GLFWMonitor
import com.mechanica.engine.display.GLFWWindow
import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window

class LwjglDisplayLoader : DisplayLoader {
    override fun createWindow(title: String, width: Int, height: Int, sharedWith: Window?): Window {
        return GLFWWindow.create(title, width, height, sharedWith)
    }

    override fun createWindow(title: String, monitor: Monitor, sharedWith: Window?): Window {
        return GLFWWindow.create(title, monitor, sharedWith)
    }

    override fun createWindow(title: String, width: Int, height: Int, monitor: Monitor, sharedWith: Window?): Window {
        return GLFWWindow.create(title, width, height, monitor, sharedWith)
    }

    override val allMonitors: Array<out Monitor>
        get() = GLFWMonitor.allMonitors

    override fun getPrimaryMonitor(): Monitor {
        return GLFWMonitor.getPrimaryMonitor()
    }
}