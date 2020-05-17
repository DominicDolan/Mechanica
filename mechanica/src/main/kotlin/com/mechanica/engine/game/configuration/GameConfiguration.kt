package com.mechanica.engine.game.configuration

import com.mechanica.engine.display.Window
import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.game.view.View
import com.mechanica.engine.input.ControlsMap
import org.joml.Matrix4f
import com.mechanica.engine.state.LoadState
import com.mechanica.engine.state.State

interface GameConfiguration {
    var initalize: Boolean
    fun setResolution(width: Int, height: Int)
    fun setFullscreen(isFullscreen: Boolean)
    fun setViewport(width: Double = 0.0, height: Double = 0.0)
    fun setViewLocation(x: Double, y: Double)

    fun setStartingState(state: () -> State)
    fun setLoader(loader: () -> LoadState)

    fun setControlMapping(controlsMap: ControlsMap)
    fun setSaveData(vararg savedata: Any)

    fun setMultisampling(samples: Int)

    fun configureWindow(configuration: (Window.() -> Unit))
    fun configureDebugMode(configuration: GameDebugConfiguration.() -> Unit)

    fun configureProjectionMatrix(configuration: Matrix4f.(View) -> Unit)
}