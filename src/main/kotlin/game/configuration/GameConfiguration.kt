package game.configuration

import debug.DebugConfiguration
import display.Window
import game.view.View
import input.ControlsMap
import org.joml.Matrix4f
import state.LoadState
import state.State

interface GameConfiguration {
    var initaliize: Boolean
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
    fun configureDebugMode(configuration: DebugConfiguration.() -> Unit)

    fun configureProjectionMatrix(configuration: Matrix4f.(View) -> Unit)
}