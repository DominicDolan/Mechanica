package com.mechanica.engine.game.configuration

import com.mechanica.engine.context.GLFWContext
import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.display.Window
import com.mechanica.engine.game.delta.DeltaCalculator
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.Scene
import org.joml.Matrix4f

internal class GameConfigurationImpl(private val configure: GameConfiguration.() -> Unit) : GameConfiguration {
    val data = ConfigurationData()

    override var initalize: Boolean = true

    override fun setResolution(width: Int, height: Int) {
        data.resolutionWidth = width
        data.resolutionHeight = height
    }

    override fun setFullscreen(isFullscreen: Boolean) {
        data.fullscreen = isFullscreen
    }

    override fun setViewport(width: Double, height: Double) {
        if (width != 0.0) {
            data.viewWidth = width
        }
        if (height != 0.0) {
            data.viewHeight = height
        }
    }

    override fun setViewLocation(x: Double, y: Double) {
        data.viewX = x
        data.viewY = y
    }

    override fun setStartingScene(scene: () -> Scene) {
        data.startingScene = scene
    }

    override fun configureWindow(configuration: Window.() -> Unit) {
        data.windowConfiguration = configuration
    }

    override fun configureDebugMode(configuration: GameDebugConfiguration.() -> Unit) {
        data.debugConfiguration = configuration
    }

    override fun setMultisampling(samples: Int) {
        GLFWContext.multisampling(samples)
    }

    override fun configureProjectionMatrix(configuration: Matrix4f.(View) -> Unit) {
        data.projectionMatrixConfiguration = configuration
    }

    override fun setDeltaTimeCalculator(calculator: DeltaCalculator) {
        data.deltaCalculator = calculator
    }

    fun configure() {
        this.configure.invoke(this)
    }
}