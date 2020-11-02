package com.mechanica.engine.game.configuration

import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window
import com.mechanica.engine.game.delta.DeltaCalculator
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.Scene
import org.joml.Matrix4f

class ConfigurationData {
    val monitor: Monitor? = null

    var title: String = "Mechanica"
    
    var resolutionWidth: Int? = null
    var resolutionHeight: Int? = null
    
    var viewWidth: Double? = null
    var viewHeight: Double? = null
    
    var viewX: Double = 0.0
    var viewY: Double = 0.0

    var fullscreen: Boolean = true

    var startingScene: (() -> Scene)? = null

    var windowConfiguration: (Window.() -> Unit) = { }
    var debugConfiguration: (GameDebugConfiguration.() -> Unit) = { }
    var projectionMatrixConfiguration: (Matrix4f.(View) -> Unit) = GameMatrices.Companion::defaultProjectionMatrix

    var deltaCalculator: DeltaCalculator = DeltaCalculator.basicVariableCalculator()
}