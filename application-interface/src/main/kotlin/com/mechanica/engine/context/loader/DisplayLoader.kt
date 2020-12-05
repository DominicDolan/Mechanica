package com.mechanica.engine.context.loader

import com.mechanica.engine.context.MechanicaInitializer
import com.mechanica.engine.display.DesktopWindow
import com.mechanica.engine.display.Display

interface DisplayLoader {
    fun createWindow(title: String, width: Int, height: Int, sharedWith: DesktopWindow? = null): DesktopWindow
    fun createWindow(title: String, display: Display, sharedWith: DesktopWindow? = null): DesktopWindow
    fun createWindow(title: String, width: Int, height: Int, display: Display, sharedWith: DesktopWindow? = null): DesktopWindow

    val allDisplays: Array<out Display>

    fun getPrimaryMonitor(): Display

    fun multisampling(samples: Int)

    companion object : DisplayLoader by MechanicaInitializer.loader.displayLoader
}