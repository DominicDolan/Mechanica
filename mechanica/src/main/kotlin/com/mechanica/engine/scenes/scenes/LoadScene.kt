package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer

abstract class LoadScene : MainScene() {
    private val waitTime = 0.2f
    private val minLoops = 2

    private var currentLoops = 0
    private var currentWait = 0.0
    private var startLoading = false
    private var finishedLoading = false

    private var hasPreLoaded = false

    override fun update(delta: Double) {
        if (!hasPreLoaded) {
            hasPreLoaded = true
            preLoad()
        }
        currentWait += delta
        currentLoops++
        startLoading = currentWait > waitTime && currentLoops > minLoops
        if (finishedLoading) onFinish()

    }

    override fun render(draw: Drawer) {
        renderLoadScreen(draw)
        if (startLoading && !finishedLoading) {
            load()
            finishedLoading = true
        }

    }

    abstract fun preLoad()
    abstract fun renderLoadScreen(draw: Drawer)

    abstract fun load()

    abstract fun onFinish()
}