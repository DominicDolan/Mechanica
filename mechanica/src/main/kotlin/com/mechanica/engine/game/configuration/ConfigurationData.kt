package com.mechanica.engine.game.configuration

import com.mechanica.engine.configuration.ContextConfigurationData
import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.display.Display
import com.mechanica.engine.display.DrawSurface
import com.mechanica.engine.game.delta.DeltaCalculator
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.Scene
import org.joml.Matrix4f

class ConfigurationData : ContextConfigurationData {
    override val display: Display? = null

    override var title: String = "Mechanica"

    override var resolutionWidth: Int? = null
    override var resolutionHeight: Int? = null

    override var viewWidth: Double? = null
    override var viewHeight: Double? = null

    override var viewX: Double = 0.0
    override var viewY: Double = 0.0

    override var fullscreen: Boolean = true

    override var surfaceConfiguration: DrawSurface.() -> Unit = { }
    override var multisamplingSamples: Int = 4

    var startingScene: (() -> Scene)? = null

    var debugConfiguration: (GameDebugConfiguration.() -> Unit) = { }
    var projectionMatrixConfiguration: (Matrix4f.(View) -> Unit) = GameMatrices.Companion::defaultProjectionMatrix

    var deltaCalculator: DeltaCalculator = DeltaCalculator.basicVariableCalculator()
}