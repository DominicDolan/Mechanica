package game

import data.loadData
import data.saveData
import display.GLFWContext
import display.Window
import game.configuration.GameConfiguration
import game.configuration.GameConfigurationImpl
import game.configuration.GameSetup
import game.view.GameMatrices
import game.view.GameView
import game.view.Matrices
import gl.utils.GLContext
import org.lwjgl.opengl.GL11
import state.State
import util.Timer

object Game2 {
    private val configuration = GameConfigurationImpl()
    private val data by lazy { configuration.data }

    val view: GameView by lazy { GameView(data) }
    val window: Window by lazy { data.window }

    val matrices: Matrices by lazy { GameMatrices(data) }
    private val gameMatrices: GameMatrices
        get() = matrices as GameMatrices

    private val stateManager = StateManager()

    private var hasStarted = false

    fun configure(setup: GameConfiguration.() -> Unit) {
        setup(configuration)
        if (!hasStarted) start()
    }

    fun start() {
        hasStarted = true

        GLContext.initialize(window)
        window.addRefreshCallback { refreshView(it) }

        Timer
        loadPersistenceData()
        setStartingState(data)
    }

    fun run(update: () -> Unit = { }) {
        if (!hasStarted) start()
        while (true) {
            GLContext.startFrame()

            gameMatrices.updateMatrices()

            stateManager.updateState()

            update()

            if (!window.update()) {
                finish()
                return
            }
        }
    }


    fun finish() {
        savePersistenceData()
        GLFWContext.terminate()
    }

    fun setCurrentState(setter: () -> State) {
        stateManager.setCurrentState(setter)
    }

    private fun setStartingState(data: GameSetup) {
        val state = data.startingState
        val loadState = data.loadState()
        loadState.startingState = state
        setCurrentState {
            loadState.preLoad()
            loadState
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
}