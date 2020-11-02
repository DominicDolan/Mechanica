package com.mechanica.engine.game.configuration

import com.mechanica.engine.config.BackendDebugConfiguration
import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.*
import com.mechanica.engine.persistence.populateData
import com.mechanica.engine.scenes.SceneManager
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.unit.vector.vec

internal class GameSetup(configuration: GameConfigurationImpl) {

    val monitor: Monitor

    val resolution: Resolution
    val ratio: Double
        get() = resolution.ratio

    val view: View

    val window: Window
    val debugConfig = GameDebugConfiguration()

    val cameras: Cameras

    val sceneManager: SceneManager

    init {
        val data = configuration.data

        populateData()

        configuration.configure()

        monitor = data.monitor ?: Monitor.getPrimaryMonitor()

        resolution = Resolution(data)

        window = setupWindow(data)

        view = StaticView(data.viewX, data.viewY,
                resolution.resolutionToCameraConverter.cameraWidthOut,
                resolution.resolutionToCameraConverter.cameraHeightOut)

        data.debugConfiguration(debugConfig)

        BackendDebugConfiguration.set(debugConfig)

        cameras = Cameras(data)

        sceneManager = SceneManager(data.deltaCalculator, data.startingScene ?: {object : Scene() {}})
    }

    private fun setupWindow(data: ConfigurationData): Window {
        val fullscreen = data.fullscreen
        val window = if (fullscreen && !resolution.resolutionWasConfigured) {
            Window.create(data.title, monitor)
        } else if (fullscreen && resolution.resolutionWasConfigured) {
            Window.create(data.title, resolution.width, resolution.height, monitor)
        } else {
            Window.create(data.title, resolution.width, resolution.height)
        }

        if (!fullscreen) centerWindow(window)

        data.windowConfiguration(window)

        window.addRefreshCallback { resolution.refreshView(it) }

        return window
    }

    private fun centerWindow(window: Window) {
        val monitor = Monitor.getPrimaryMonitor()
        val screenWidth = monitor.width
        val screenHeight = monitor.height
        window.position.set((screenWidth - window.width)/2, (screenHeight - window.height)/2)
    }

    inner class Resolution(data: ConfigurationData) {
        val width: Int
        val height: Int

        val resolutionWasConfigured = data.resolutionWidth != null && data.resolutionHeight != null

        val ratio: Double

        val resolutionToCameraConverter: ResolutionToCameraConverter

        init {
            val defaultResolutionWidth = (monitor.width*0.75).toInt()
            val defaultResolutionHeight = (monitor.height*0.75).toInt()

            width = data.resolutionWidth ?: defaultResolutionWidth
            height = data.resolutionHeight ?: defaultResolutionHeight

            ratio = height.toDouble()/width.toDouble()

            resolutionToCameraConverter = ResolutionToCameraConverter(width, height, data.viewWidth, data.viewHeight)

        }

        fun refreshView(window: Window) {
            val converter = resolutionToCameraConverter

            converter.calculate(Game.world, window)
            cameras.world.width = converter.cameraWidthOut

            converter.calculate(Game.ui, window)
            cameras.ui.update(converter.cameraWidthOut, converter.cameraHeightOut)
        }
    }

    inner class Cameras(data: ConfigurationData) {
        val world: WorldCamera
        val ui: UICamera

        val matrices = GameMatrices(window, data.projectionMatrixConfiguration, view)

        init {
            val contentScale = monitor.contentScale
            world = WorldCamera(view, matrices)
            ui = UICamera(matrices, world, vec(contentScale.xScale, contentScale.yScale))
        }
    }

}