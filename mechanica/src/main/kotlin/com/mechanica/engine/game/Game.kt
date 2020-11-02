package com.mechanica.engine.game

import com.mechanica.engine.configuration.Configurable
import com.mechanica.engine.context.Application
import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.debug.DebugConfiguration
import com.mechanica.engine.display.Window
import com.mechanica.engine.game.configuration.GameConfiguration
import com.mechanica.engine.game.configuration.GameConfigurationImpl
import com.mechanica.engine.game.configuration.GameSetup
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.game.view.UICamera
import com.mechanica.engine.game.view.WorldCamera
import com.mechanica.engine.matrix.Matrices
import com.mechanica.engine.persistence.storeData
import com.mechanica.engine.scenes.SceneManager
import com.mechanica.engine.scenes.processes.Updateable
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.util.Timer

object Game : Configurable<GameConfiguration> {
    private var _application: Application? = null
    val application: Application
        get() = _application ?: throw IllegalStateException("Cannot access this application object because the game context has not been initialized.\nCall Game.configureAs() before using this object")

    private lateinit var setup: GameSetup

    val world: WorldCamera
        get() = setup.cameras.world
    val ui: UICamera
        get() = setup.cameras.ui
    val window: Window
        get() = setup.window

    val debug: DebugConfiguration
        get() = setup.debugConfig

    val matrices: Matrices
        get() = gameMatrices
    private val gameMatrices: GameMatrices
        get() = setup.cameras.matrices

    internal val sceneManager: SceneManager
        get() = setup.sceneManager

    val scene: Scene
        get() = sceneManager.currentScene ?: throw UninitializedPropertyAccessException("The top level scene has not yet been initialized")

    private var hasStarted = false
    private var hasFinished = false

    fun addProcess(process: Updateable) {
        sceneManager.addProcess(process)
    }

    fun removeProcess(process: Updateable) {
        sceneManager.removeProcess(process)
    }

    fun addScene(scene: Scene) {
        sceneManager.addScene(scene)
    }

    fun removeScene(scene: Scene) {
        sceneManager.removeScene(scene)
    }

    override fun configureAs(application: Application, configure: GameConfiguration.() -> Unit) {
        this._application = application

        application.load()
        val configuration = GameConfigurationImpl(configure)
        this.setup = GameSetup(configuration)
        if (configuration.initalize) {
            start()
        }
    }

    fun start(block: () -> Unit = {}) {
        try {
            if (!hasStarted) {
                application.initialize(window, EventCallbacks.create())

                Timer
                sceneManager.startScene()

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

    private fun savePersistenceData() {
        storeData()
    }

}