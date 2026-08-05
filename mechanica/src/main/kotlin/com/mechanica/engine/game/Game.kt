package com.mechanica.engine.game

import com.mechanica.engine.configuration.Configurable
import com.mechanica.engine.context.Application
import com.mechanica.engine.context.MechanicaInitializer
import com.mechanica.engine.debug.DebugConfiguration
import com.mechanica.engine.debug.ScreenLog
import com.mechanica.engine.display.DrawSurface
import com.mechanica.engine.game.configuration.GameConfiguration
import com.mechanica.engine.game.configuration.GameConfigurationImpl
import com.mechanica.engine.game.configuration.GameSetup
import com.mechanica.engine.game.delta.DeltaCalculator
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.game.view.UICamera
import com.mechanica.engine.game.view.WorldCamera
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.persistence.PersistenceMap
import com.mechanica.engine.scenes.SceneManager
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.scenes.scenes.SceneNode
import com.mechanica.engine.util.CameraMatrices
import com.mechanica.engine.util.Timer
import java.io.File
import javax.imageio.ImageIO

object Game : Configurable<GameConfiguration> {
    private var _application: Application? = null
    val application: Application
        get() = _application ?: throw IllegalStateException("Cannot access this application object because the game context has not been initialized.\nCall Game.configureAs() before using this object")

    private lateinit var setup: GameSetup
    internal var persistenceMap: PersistenceMap? = null

    val world: WorldCamera
        get() = setup.cameras.world
    val ui: UICamera
        get() = setup.cameras.ui
    val surface: DrawSurface
        get() = application.surfaceContext.surface

    val debug: DebugConfiguration
        get() = setup.debugConfig

    val matrices: CameraMatrices
        get() = gameMatrices
    private val gameMatrices: GameMatrices
        get() = setup.cameras.matrices

    internal val sceneManager: SceneManager
        get() = setup.sceneManager

    val scene: SceneNode
        get() = sceneManager.currentScene ?: throw UninitializedPropertyAccessException("The top level scene has not yet been initialized")

    val deltaCalculator: DeltaCalculator
        get() = setup.deltaCalculator

    private var hasStarted = false
    private var hasFinished = false

    fun addScene(scene: SceneNode) {
        sceneManager.addScene(scene)
    }

    fun removeScene(scene: Scene) {
        sceneManager.removeScene(scene)
    }

    override fun configureAs(application: Application, configure: GameConfiguration.() -> Unit) {
        this._application = application

        MechanicaInitializer.initialize(application.createFactory())

        val configuration = GameConfigurationImpl(configure)
        this.setup = GameSetup(application, configuration)
        Game.application.glContext.startFrame()
        surface.update()
        gameMatrices.updateMatrices()

        if (configuration.initalize) {
            start()
        }
    }

    fun start(block: () -> Unit = {}) {
        try {
            if (!hasStarted) {
                Timer
                sceneManager.startScene()
                updateFrame()

                hasStarted = true
            }
            block()
        } catch (ex: Exception) {
            surface.destroy()
            terminate()
            throw ex
        }
    }

    fun loop(update: ((Double) -> Unit)? = null) {
        start()
        sceneManager.updateVar = update

        try {
            while (!hasFinished) {
                if (!updateFrame()) {
                    return
                }
            }
        } finally {
            surface.destroy()
            terminate()
        }
    }

    private fun updateFrame(): Boolean {
        application.glContext.startFrame()

        gameMatrices.updateMatrices()

        checkDebugKeys()

        sceneManager.updateAndRender()

        return surface.update()
    }

    /**
     * Polled here, outside the scene tree, so the controls keep working while updates are paused.
     *
     * [com.mechanica.engine.input.Key.hasBeenPressed] is edge triggered and changes state when it
     * is read, so this must remain the only place these keys are polled.
     */
    private fun checkDebugKeys() {
        if (!debug.debugMode) return

        if (Keyboard.F6.hasBeenPressed) debug.pauseUpdates(!debug.isPaused)
        if (Keyboard.F7.hasBeenPressed) debug.stepFrames(1)
        if (Keyboard.F8.hasBeenPressed) debug.stepFrames(10)

        if (debug.isPaused) ScreenLog { "PAUSED   F7 step   F8 step x10   F6 resume" }
    }

    fun screenshot() {
        val image = application.glContext.screenshot()
        // Save the image to a file
        try {
            val filepath = "./test.png"
            // Make sure parent directories exist

            File(filepath).parentFile?.mkdirs()

            ImageIO.write(image, "PNG", File(filepath))
            println("Screenshot saved successfully to: $filepath")
        } catch (e: Exception) {
            println("Failed to save screenshot: ${e.message}")
        }
    }
    fun close() {
        surface.shouldClose()
    }

    fun terminate() {
        savePersistenceData()
        hasFinished = true
        application.terminate()
    }

    fun setMainScene(setter: () -> SceneNode?) {
        sceneManager.setMainScene(setter)
    }

    private fun savePersistenceData() {
        persistenceMap?.store()
    }

}