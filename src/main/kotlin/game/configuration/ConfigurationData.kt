package game.configuration

import debug.DebugConfiguration
import display.Monitor
import display.Window
import drawer.Drawer
import input.ControlsMap
import state.LoadState
import state.State

interface ConfigurationData {
    val monitor: Monitor?
    val title: String?
    val resolutionWidth: Int?
    val resolutionHeight: Int?
    val viewWidth: Double?
    val viewHeight: Double?
    val viewX: Double?
    val viewY: Double?
    val saveData: Array<Any>?
    val controlsMap: ControlsMap?
    val fullscreen: Boolean?
    val startingState: (() -> State)?
    val loadState: (() -> LoadState)?
    val windowConfiguration: (Window.() -> Unit)?
    val debugConfiguration: (DebugConfiguration.() -> Unit)?

    companion object {
        fun emptyState(): State {
            return object : State(){
                override fun update(delta: Double) {}
                override fun render(draw: Drawer) {}
            }
        }

        fun emptyLoadeState(): LoadState {
            return object : LoadState(){
                override fun preLoad() {}
                override fun renderLoadScreen(g: Drawer) {}
                override fun load() {}
            }
        }
    }
}