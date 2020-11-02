package com.mechanica.engine.context

import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.display.Window

interface Application {
    fun createLoader(): GLLoader

    fun load() {
        GLInitializer.initialize(createLoader())
    }

    fun initialize(mainWindow: Window, callbacks: EventCallbacks)
    fun terminate()

    fun startFrame()

    fun activateContext(window: Window?)

}