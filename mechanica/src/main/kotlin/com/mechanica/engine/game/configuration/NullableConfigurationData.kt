package com.mechanica.engine.game.configuration

import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.MainScene
import org.joml.Matrix4f

class NullableConfigurationData : ConfigurationData {
    override val monitor: Monitor? = null

    override var title: String? = null
    
    override var resolutionWidth: Int? = null
    override var resolutionHeight: Int? = null
    
    override var viewWidth: Double? = null
    override var viewHeight: Double? = null
    
    override var viewX: Double? = null
    override var viewY: Double? = null

    override var fullscreen: Boolean? = null

    override var startingScene: (() -> MainScene)? = null

    override var windowConfiguration: (Window.() -> Unit)? = null
    override var debugConfiguration: (GameDebugConfiguration.() -> Unit)? = null
    override var projectionMatrixConfiguration: (Matrix4f.(View) -> Unit)? = null
}