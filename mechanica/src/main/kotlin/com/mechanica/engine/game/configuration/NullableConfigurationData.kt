package com.mechanica.engine.game.configuration

import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window
import debug.GameDebugConfiguration
import com.mechanica.engine.game.view.View
import com.mechanica.engine.input.ControlsMap
import org.joml.Matrix4f
import com.mechanica.engine.state.LoadState
import com.mechanica.engine.state.State

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
    override var debugConfiguration: (GameDebugConfiguration.() -> Unit)? = null
    override var projectionMatrixConfiguration: (Matrix4f.(View) -> Unit)? = null
}