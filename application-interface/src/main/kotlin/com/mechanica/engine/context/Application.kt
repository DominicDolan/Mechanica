package com.mechanica.engine.context

import com.mechanica.engine.display.Window

interface Application {

    fun initialize(window: Window)

    fun terminate()

    fun startFrame()

}