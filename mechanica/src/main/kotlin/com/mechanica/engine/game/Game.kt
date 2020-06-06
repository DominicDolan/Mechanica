package com.mechanica.engine.game

import com.mechanica.engine.config.BackendDebugConfiguration
import com.mechanica.engine.context.ALContext
import com.mechanica.engine.context.GLFWContext
import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window
import com.mechanica.engine.context.GLContext
import com.mechanica.engine.persistence.loadData
import com.mechanica.engine.persistence.saveData
import com.mechanica.engine.game.configuration.GameConfiguration
import com.mechanica.engine.game.configuration.GameConfigurationImpl
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.game.view.GameView
import com.mechanica.engine.game.view.View
import com.mechanica.engine.context.GLInitializer
import com.mechanica.engine.context.loader.LwjglLoader
import com.mechanica.engine.matrix.Matrices
import com.mechanica.engine.scenes.SceneManager
import com.mechanica.engine.scenes.processes.Process
import com.mechanica.engine.scenes.scenes.*
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

    private val sceneManager = SceneManager()
    val scene: Scene
        get() = sceneManager.currentScene ?: throw UninitializedPropertyAccessException("The top level scene has not yet been initialized")

    private var hasStarted = false
    private var hasFinished = false

    fun addProcess(process: Process) {
        sceneManager.addProcess(process)
    }

    fun addScene(scene: Scene) {
        sceneManager.addScene(scene)
    }

    fun configure(setup: GameConfiguration.() -> Unit) {
        setup(configuration)
        if (configuration.initalize) start()
        BackendDebugConfiguration.set(debug)
    }

    fun start(block: () -> Unit = {}) {
        try {
            if (!hasStarted) {
                GLContext.initialize(window)
                val callbacks = GLInitializer.initialize(LwjglLoader())
                GLContext.setCallbacks(window, callbacks)
                ALContext.initialize()
                window.addRefreshCallback { refreshView(it) }

                Timer
                loadPersistenceData()
                sceneManager.setStartingScene(data)

                hasStarted = true
            }
            block()
        } catch (ex: Exception) {
            window.destroy()
            terminate()
            throw ex
        }
    }

    fun run() {
        start()
        loop()
    }

    fun run(update: (Double) -> Unit) {
        start()
        sceneManager.updateVar = update
        loop()
    }

    private fun loop() {
        try {
            while (!hasFinished) {
                GLContext.startFrame()

                gameMatrices.updateMatrices()

                sceneManager.updateScenes()

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
        ALContext.destroy()
    }

    fun setMainScene(setter: () -> MainScene?) {
        sceneManager.setMainScene(setter)
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
        override val height: Double
        override val x: Double = 0.0
        override val y: Double = 0.0
        override val center: Vector = vec(0.0, 0.0)
        override val ratio: Double
            get() = view.ratio*(scale.y/scale.x)

        init {
            val contentScale = Monitor.getPrimaryMonitor().contentScale
            scale = vec(contentScale.xScale, contentScale.yScale)
            width = view.width/scale.x
            height = view.height/scale.y
        }
    }
}