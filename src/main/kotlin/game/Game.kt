package game

import data.loadData
import data.saveData
import debug.DebugDrawer
import display.GLFWContext
import display.Window
import game.configuration.GameConfiguration
import game.configuration.GameConfigurationImpl
import game.configuration.GameSetup
import game.view.GameMatrices
import game.view.GameView
import game.view.Matrices
import gl.utils.GLContext
import state.State
import util.Timer

object Game {
    private val configuration = GameConfigurationImpl()
    private val data by lazy { configuration.data }

    val view: GameView by lazy { GameView(data) }
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
        if (configuration.initaliize) start()
    }

    fun start() {
        if (!hasStarted) {
            hasStarted = true

            GLContext.initialize(window)
            window.addRefreshCallback { refreshView(it) }

            Timer
            loadPersistenceData()
            setStartingState(data)
        }
    }

    fun run(update: () -> Unit = { }) {
        start()
        while (!hasFinished) {
            GLContext.startFrame()

            gameMatrices.updateMatrices()

            stateManager.updateState()

            update()

            if (!window.update()) {
                terminate()
                return
            }
        }
    }


    fun close() {
        window.shouldClose = true
    }

    fun terminate() {
        savePersistenceData()
        hasFinished = true
        GLFWContext.terminate()
    }

    fun setCurrentState(setter: () -> State) {
        stateManager.setCurrentState(setter)
    }

    private fun setStartingState(data: GameSetup) {
        val state = data.startingState
        val loadState = data.loadState()
        loadState.onFinish = {
            setCurrentState { state() }
        }
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