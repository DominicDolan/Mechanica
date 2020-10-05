package com.mechanica.engine.scenes.processes

import com.mechanica.engine.display.Window
import com.mechanica.engine.game.Game

abstract class LoadProcess(order: Int = 0, private val waitTime: Float = 0.2f, private val waitLoops: Int = 2) : Process(order) {

    private val loadingContext = Window.create("", 640, 480, Game.window)

    private var stage = Stage.WAIT

    private var currentLoops = 0
    private var currentWait = 0.0

    private var exception: Throwable? = null

    override fun updateNodes(delta: Double) {
        when (stage) {
            Stage.WAIT -> wait(delta)
            Stage.FINISHED_WAIT -> startLoading()
            Stage.LOADING -> whileLoading (delta)
            Stage.FINISHED_LOADING -> {
                val exception = this.exception
                if (exception != null) {
                    this.exception = null
                    throw exception
                }
                onFinish()
            }
        }
        super.updateNodes(delta)
    }

    private fun wait(delta: Double) {
        currentWait += delta
        currentLoops++
        if (currentWait > waitTime && currentLoops > waitLoops) {
            stage = Stage.FINISHED_WAIT
        }
    }

    private fun startLoading() {
        val runnable = Runnable {
            Game.application.activateContext(loadingContext)
            try {
                load()
            } finally {
                stage = Stage.FINISHED_LOADING
            }
        }

        val thread = Thread(runnable)

        stage = Stage.LOADING
        thread.setUncaughtExceptionHandler { _, e -> exception = e }
        thread.start()
    }

    abstract fun load()

    open fun whileLoading(delta: Double) { }

    abstract fun onFinish()

    enum class Stage {
        WAIT,
        FINISHED_WAIT,
        LOADING,
        FINISHED_LOADING
    }
}