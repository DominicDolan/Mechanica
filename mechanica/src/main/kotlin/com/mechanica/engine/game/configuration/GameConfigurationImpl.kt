package com.mechanica.engine.game.configuration

import com.mechanica.engine.context.GLFWContext
import com.mechanica.engine.display.Window
import debug.GameDebugConfiguration
import com.mechanica.engine.game.view.View
import com.mechanica.engine.input.ControlsMap
import org.joml.Matrix4f
import com.mechanica.engine.state.LoadState
import com.mechanica.engine.state.State

internal class GameConfigurationImpl : GameConfiguration {
    private val _data = NullableConfigurationData()
    val data: GameSetup
        get() = GameSetup(_data)

    override var initalize: Boolean = true

    override fun setResolution(width: Int, height: Int) {
        _data.resolutionWidth = width
        _data.resolutionHeight = height
    }

    override fun setFullscreen(isFullscreen: Boolean) {
        _data.fullscreen = isFullscreen
    }

    override fun setViewport(width: Double, height: Double) {
        if (width != 0.0) {
            _data.viewWidth = width
        }
        if (height != 0.0) {
            _data.viewHeight = height
        }
    }

    override fun setViewLocation(x: Double, y: Double) {
        _data.viewX = x
        _data.viewY = y
    }

    override fun setStartingState(state: () -> State) {
        _data.startingState = state
    }

    override fun setLoader(loader: () -> LoadState) {
        _data.loadState = loader
    }

    override fun setControlMapping(controlsMap: ControlsMap) {
        _data.controlsMap = controlsMap
    }

    override fun setSaveData(vararg savedata: Any) {
        _data.saveData = arrayOf(savedata)
    }

    override fun configureWindow(configuration: Window.() -> Unit) {
        _data.windowConfiguration = configuration
    }

    override fun configureDebugMode(configuration: GameDebugConfiguration.() -> Unit) {
        _data.debugConfiguration = configuration
    }

    override fun setMultisampling(samples: Int) {
        GLFWContext.multisampling(samples)
    }

    override fun configureProjectionMatrix(configuration: Matrix4f.(View) -> Unit) {
        _data.projectionMatrixConfiguration = configuration
    }
}