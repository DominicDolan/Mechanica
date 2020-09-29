package com.mechanica.engine.context

import com.mechanica.engine.display.Window

interface Application {

    fun initialize(mainWindow: Window)

    fun terminate()

    fun startFrame()

    fun activateContext(window: Window?)

}