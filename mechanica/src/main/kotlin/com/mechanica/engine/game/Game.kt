package com.mechanica.engine.game

import com.mechanica.engine.config.BackendDebugConfiguration
import com.mechanica.engine.context.GLFWContext
import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window
import com.mechanica.engine.context.GLContext
import com.mechanica.engine.persistence.loadData
import com.mechanica.engine.persistence.saveData
import com.mechanica.engine.game.configuration.GameConfiguration
import com.mechanica.engine.game.configuration.GameConfigurationImpl
import com.mechanica.engine.game.configuration.GameSetup
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.game.view.GameView
import com.mechanica.engine.game.view.View
import com.mechanica.engine.context.GLInitializer
import com.mechanica.engine.context.loader.LwjglLoader
import com.mechanica.engine.matrix.Matrices
import com.mechanica.engine.state.State
import com.mechanica.engine.state.StateManager
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.util.Timer

object Game {
    private val configuration = GameConfigurationImpl()
    private val data by lazy { configuration.data }

    val view: GameView by lazy { GameView(data) }
    val ui: View by lazy { UIView() }
    val window: Window by lazy { data.window }

    internal val controls by lazy { data.controlsMap }

    val debug by lazy { data.debugConfig }

    val matrices: Matrices by lazy { GameMatrices(data, view) }
    private val gameMatrices: GameMatrices
        get() = matrices as GameMatrices

    private val stateManager = StateManager()

    private var hasStarted = false
    private var hasFinished = false

    fun configure(setup: GameConfiguration.() -> Unit) {
        setup(configuration)
        if (configuration.initalize) start()
        BackendDebugConfiguration.set(debug)
    }

    fun start() {
        if (!hasStarted) {
            hasStarted = true

            GLContext.initialize(window)
            GLInitializer.initialize(LwjglLoader())
            window.addRefreshCallback { refreshView(it) }

            Timer
            loadPersistenceData()
            setStartingState(data)
        }
    }

    fun run(update: () -> Unit = { }) {
        start()
        try {
            while (!hasFinished) {
                GLContext.startFrame()

                gameMatrices.updateMatrices()

                stateManager.updateState()

                update()

                if (!window.update()) {
                    return
                }
            }
        } catch (ex: Exception) {
            throw ex
        } finally {
            window.destroy()
            terminate()
        }
    }


    fun close() {
        window.shouldClose = true
    }

    fun terminate() {
        savePersistenceData()
        hasFinished = true
        GLContext.free()
        GLFWContext.terminate()
    }

    fun setCurrentState(setter: () -> State?) {
        stateManager.setCurrentState(setter)
    }

    private fun setStartingState(data: GameSetup) {
        val state = data.startingState
        val loadState = data.loadState?.invoke()

        if (loadState != null) {
            loadState.onFinish = {
                setCurrentState { state?.invoke() }
            }
            setCurrentState {
                loadState.preLoad()
                loadState
            }
        } else {
            setCurrentState { state?.invoke() }
        }
    }

    private fun refreshView(window: Window) {
        val converter = data.resolutionConverter
        if (converter.viewHeight != null) {
            converter.viewHeight = view.height
        }
        if (converter.viewWidth != null) {
            converter.viewWidth = view.width
        }
        converter.resolutionWidth = window.width
        converter.resolutionHeight = window.height
        converter.calculate()
        view.width = converter.viewWidthOut
    }

    private fun loadPersistenceData() {
        for (saveData in data.saveData) {
            loadData(saveData)
        }
    }

    private fun savePersistenceData() {
        for (saveData in data.saveData) {
            saveData(saveData)
        }
    }

    private class UIView : View {
        private val scale: Vector
        override val width: Double
            get() = view.width/scale.x
        override val height: Double
            get() = view.height/scale.y
        override val x: Double = 0.0
        override val y: Double = 0.0
        override val center: Vector = vec(0.0, 0.0)
        override val ratio: Double
            get() = view.ratio*(scale.y/scale.x)

        init {
            val contentScale = Monitor.getPrimaryMonitor().contentScale
            scale = vec(contentScale.xScale, contentScale.yScale)
        }
    }
}