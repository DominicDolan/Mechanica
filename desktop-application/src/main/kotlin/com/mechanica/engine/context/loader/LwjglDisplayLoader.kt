package com.mechanica.engine.context.loader

import com.mechanica.engine.display.DesktopWindow
import com.mechanica.engine.display.Display
import com.mechanica.engine.display.GLFWMonitor
import com.mechanica.engine.display.GLFWWindow

class LwjglDisplayLoader : DisplayLoader {
    override fun createWindow(title: String, width: Int, height: Int, sharedWith: DesktopWindow?): DesktopWindow {
        return GLFWWindow.create(title, width, height, sharedWith)
    }

    override fun createWindow(title: String, display: Display, sharedWith: DesktopWindow?): DesktopWindow {
        return GLFWWindow.create(title, display, sharedWith)
    }

    override fun createWindow(title: String, width: Int, height: Int, display: Display, sharedWith: DesktopWindow?): DesktopWindow {
        return GLFWWindow.create(title, width, height, display, sharedWith)
    }

    override val allDisplays: Array<out Display>
        get() = GLFWMonitor.allMonitors

    override fun getPrimaryMonitor(): Display {
        return GLFWMonitor.getPrimaryMonitor()
    }
}