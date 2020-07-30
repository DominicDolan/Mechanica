package com.mechanica.engine.context.loader

import com.mechanica.engine.context.GLInitializer
import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window

interface DisplayLoader {
    fun createWindow(title: String, width: Int, height: Int): Window
    fun createWindow(title: String, monitor: Monitor): Window
    fun createWindow(title: String, width: Int, height: Int, monitor: Monitor): Window

    val allMonitors: Array<out Monitor>

    fun getPrimaryMonitor(): Monitor

    companion object : DisplayLoader by GLInitializer.displayLoader
}