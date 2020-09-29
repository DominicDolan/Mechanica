package com.mechanica.engine.game

import com.mechanica.engine.config.BackendDebugConfiguration
import com.mechanica.engine.configuration.Configurable
import com.mechanica.engine.context.Application
import com.mechanica.engine.display.Window
import com.mechanica.engine.game.configuration.GameConfiguration
import com.mechanica.engine.game.configuration.GameConfigurationImpl
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.game.view.UICamera
import com.mechanica.engine.game.view.WorldCamera
import com.mechanica.engine.matrix.Matrices
import com.mechanica.engine.persistence.populateData
import com.mechanica.engine.persistence.storeData
import com.mechanica.engine.scenes.SceneManager
import com.mechanica.engine.scenes.processes.Updateable
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.util.Timer

object Game : Configurable<GameConfiguration> {
    private var _application: Application? = null
    val application: Application
        get() = _application ?: throw IllegalStateException("Cannot access this application object because the game context has not been initialized.\nCall Game.configureAs() before using this object")

    private val configuration = GameConfigurationImpl()

    private val data by lazy { configuration.data }

    val world: WorldCamera by lazy { WorldCamera(data) }
    val ui: UICamera by lazy { UICamera() }
    val window: Window by lazy { data.window }

    val debug by lazy { data.debugConfig }

    val matrices: Matrices by lazy { GameMatrices(data, world) }
    private val gameMatrices: GameMatrices
        get() = matrices as GameMatrices

    internal val sceneManager by lazy { SceneManager(data) }

    val scene: Scene
        get() = sceneManager.currentScene ?: throw UninitializedPropertyAccessException("The top level scene has not yet been initialized")

    private var hasStarted = false
    private var hasLoadedPersistence = false
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
        BackendDebugConfiguration.set(debug)
        if (configuration.initalize) {
            start()
        }
    }

    fun initializeAs(application: Application) {
        this._application = application
        loadPersistenceData()
        configuration.initalize = false
        start()
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

    fun loop(update: ((Double) -> Unit)? = null) {
        start()
        sceneManager.updateVar = update

        try {
            while (!hasFinished) {
                updateFrame()
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

    private fun updateFrame() {
        application.startFrame()

        gameMatrices.updateMatrices()

        sceneManager.updateAndRender()
    }

    fun close() {
        window.shouldClose = true
    }

    fun terminate() {
        savePersistenceData()
        hasFinished = true
        application.terminate()
    }

    fun setMainScene(setter: () -> Scene?) {
        sceneManager.setMainScene(setter)
    }

    private fun refreshView(window: Window) {
        val converter = data.resolutionConverter

        converter.calculate(world, window)
        world.width = converter.viewWidthOut

        converter.calculate(ui, window)
        ui._width = converter.viewWidthOut
        ui._height = converter.viewHeightOut
        gameMatrices.setUiView(ui._height)
    }

    fun loadPersistenceData() {
        if (!hasLoadedPersistence) {
            populateData()
            hasLoadedPersistence = true
        }
    }

    private fun savePersistenceData() {
        storeData()
    }

}