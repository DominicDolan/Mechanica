package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.game.Game
import com.mechanica.engine.context.loader.BackgroundTask
import com.mechanica.engine.context.loader.MechanicaFactory

abstract class LoadScene(private val waitTime: Float = 0.2f, private val waitLoops: Int = 2) : Scene() {

    private val loadingContext = Game.application.surfaceContext.createSharedContext()

    private var stage = Stage.WAIT

    private var currentLoops = 0
    private var currentWait = 0.0

    private var loadingTask: BackgroundTask? = null

    private var onFinishCalled = false
    override fun update(delta: Double) {
        if (onFinishCalled) return
        when (stage) {
            Stage.WAIT -> wait(delta)
            Stage.FINISHED_WAIT -> startLoading()
            Stage.LOADING -> {
                val task = loadingTask!!
                if (task.isFinished) {
                    task.failure?.let { throw it }
                    stage = Stage.FINISHED_LOADING
                } else whileLoading(delta)
            }
            Stage.FINISHED_LOADING -> {
                onFinish()
                onFinishCalled = true
            }
        }
    }

    private fun wait(delta: Double) {
        currentWait += delta
        currentLoops++
        if (currentWait > waitTime && currentLoops > waitLoops) {
            stage = Stage.FINISHED_WAIT
        }
    }

    private fun startLoading() {
        stage = Stage.LOADING
        loadingTask = MechanicaFactory.miscFactory.startBackgroundTask {
            loadingContext.activate()
            load()
        }
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