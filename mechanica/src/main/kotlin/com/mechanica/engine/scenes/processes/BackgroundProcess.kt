package com.mechanica.engine.scenes.processes

import com.mechanica.engine.display.Window
import com.mechanica.engine.game.Game

abstract class BackgroundProcess(private val sleepSeconds: Double) : Process() {

    private val backgroundContext = Window.create("", 640, 480, Game.window)

    private var exception: Throwable? = null

    private var running = false
    private var terminated = false

    private fun startBackgroundProcess() {
        val runnable = Runnable {
            Game.application.activateContext(backgroundContext)
            try {
                backgroundLoop()
            } finally {
                running = false
            }
        }

        val thread = Thread(runnable)

        thread.setUncaughtExceptionHandler { _, e -> exception = e }
        running = true
        thread.start()
    }

    private fun backgroundLoop() {
        val sleepMillis = (sleepSeconds*1000.0).toLong()
        while (running && !terminated) {
            backgroundUpdate()
            Thread.sleep(sleepMillis)
        }
    }

    abstract fun backgroundUpdate()

    open fun handleException(exception: Throwable) {
        throw exception
    }

    override fun updateNodes(delta: Double) {
        super.updateNodes(delta)

        if (!running) {
            startBackgroundProcess()
        }

        val exception = this.exception

        if (exception != null) {
            try {
                handleException(exception)
            } finally {
                this.exception = null
            }
        }
    }

    override fun destructNodes() {
        terminated = true
        super.destructNodes()
    }
}