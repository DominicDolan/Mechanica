package game.configuration

import debug.DebugConfiguration
import display.Window
import input.ControlsMap
import state.LoadState
import state.State

interface GameConfiguration {
    fun setResolution(width: Int, height: Int)
    fun setFullscreen(isFullscreen: Boolean)
    fun setViewport(width: Double = 0.0, height: Double = 0.0)
    fun setViewLocation(x: Double, y: Double)

    fun setStartingState(state: () -> State)
    fun setLoader(loader: () -> LoadState)

    fun setControlMapping(controlsMap: ControlsMap)
    fun setSaveData(vararg savedata: Any)

    fun configureWindow(configuration: (Window.() -> Unit))
    fun configureDebugMode(configuration: DebugConfiguration.() -> Unit)
}