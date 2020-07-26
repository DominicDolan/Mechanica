package com.mechanica.engine.game

import com.mechanica.engine.config.BackendDebugConfiguration
import com.mechanica.engine.configuration.Configurable
import com.mechanica.engine.context.Application
import com.mechanica.engine.display.Window
import com.mechanica.engine.game.configuration.GameConfiguration
import com.mechanica.engine.game.configuration.GameConfigurationImpl
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.game.view.UIView
import com.mechanica.engine.game.view.WorldView
import com.mechanica.engine.matrix.Matrices
import com.mechanica.engine.persistence.populateData
import com.mechanica.engine.persistence.storeData
import com.mechanica.engine.scenes.SceneManager
import com.mechanica.engine.scenes.processes.Updateable
import com.mechanica.engine.scenes.scenes.MainScene
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.util.Timer

object Game : Configurable<GameConfiguration> {
    private var _application: Application? = null
    val application: Application
        get() = _application ?: throw IllegalStateException("Cannot access this application object because the game context has not been initialized.\nCall Game.configureAs() before using this object")

    private val configuration = GameConfigurationImpl()

    private val data by lazy { configuration.data }

    val view: WorldView by lazy { WorldView(data) }
    val ui: UIView by lazy { UIView() }
    val window: Window by lazy { data.window }

    val debug by lazy { data.debugConfig }

    val matrices: Matrices by lazy { GameMatrices(data, view) }
    private val gameMatrices: GameMatrices
        get() = matrices as GameMatrices

    internal val sceneManager by lazy { SceneManager(data) }

    val scene: Scene
        get() = sceneManager.currentScene ?: throw UninitializedPropertyAccessException("The top level scene has not yet been initialized")

    private var hasStarted = false
    private var hasFinished = false

    fun addProcess(process: Updateable) {
        sceneManager.addProcess(process)
    }

    fun addScene(scene: Scene) {
        sceneManager.addScene(scene)
    }

    override fun configureAs(application: Application, setup: GameConfiguration.() -> Unit) {
        this._application = application
        loadPersistenceData()
        setup(configuration)
        if (configuration.initalize) start()
        BackendDebugConfiguration.set(debug)
    }

    fun start(block: () -> Unit = {}) {
        try {
            if (!hasStarted) {
                application.initialize(window)

                window.addRefreshCallback { refreshView(it) }

                Timer
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
                application.startFrame()

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
        application.terminate()
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
        populateData()
    }

    private fun savePersistenceData() {
        storeData()
    }

}