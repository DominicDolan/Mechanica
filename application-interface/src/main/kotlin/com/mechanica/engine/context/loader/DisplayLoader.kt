package com.mechanica.engine.context.loader

import com.mechanica.engine.context.GLInitializer
import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window

interface DisplayLoader {
    fun createWindow(title: String, width: Int, height: Int, sharedWith: Window? = null): Window
    fun createWindow(title: String, monitor: Monitor, sharedWith: Window? = null): Window
    fun createWindow(title: String, width: Int, height: Int, monitor: Monitor, sharedWith: Window? = null): Window

    val allMonitors: Array<out Monitor>

    fun getPrimaryMonitor(): Monitor

    companion object : DisplayLoader by GLInitializer.displayLoader
}