package game.configuration

import debug.DebugConfiguration
import display.Monitor
import display.Window
import game.configuration.ConfigurationData
import input.ControlsMap
import state.LoadState
import state.State

class NullableConfigurationData : ConfigurationData {
    override val monitor: Monitor? = null

    override var title: String? = null
    
    override var resolutionWidth: Int? = null
    override var resolutionHeight: Int? = null
    
    override var viewWidth: Double? = null
    override var viewHeight: Double? = null
    
    override var viewX: Double? = null
    override var viewY: Double? = null
    
    override var saveData: Array<Any>? = null
    override var controlsMap: ControlsMap? = null
    
    override var fullscreen: Boolean? = null

    override var startingState: (() -> State)? = null
    override var loadState: (() -> LoadState)? = null

    override var windowConfiguration: (Window.() -> Unit)? = null
    override var debugConfiguration: (DebugConfiguration.() -> Unit)? = null
    
}