package game

import data.loadData
import data.saveData
import debug.DebugDrawer
import display.GLFWContext
import display.Monitor
import display.Window
import game.configuration.GameConfiguration
import game.configuration.GameConfigurationImpl
import game.configuration.GameSetup
import game.view.GameMatrices
import game.view.GameView
import game.view.Matrices
import game.view.View
import gl.utils.GLContext
import state.State
import util.Timer
import util.extensions.vec
import util.units.Vector

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