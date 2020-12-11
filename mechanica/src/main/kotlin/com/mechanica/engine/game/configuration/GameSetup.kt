package com.mechanica.engine.game.configuration

import com.mechanica.engine.context.Application
import com.mechanica.engine.context.SurfaceContext
import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.display.DrawSurface
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.*
import com.mechanica.engine.scenes.SceneManager
import com.mechanica.engine.unit.vector.vec

internal class GameSetup(application: Application, configuration: GameConfigurationImpl) {

    val view: View

    val debugConfig = GameDebugConfiguration()

    val cameras: Cameras

    val sceneManager: SceneManager


    private val resolutionToCameraConverter: ResolutionToCameraConverter

    init {
        val data = configuration.data

        configuration.configure()

        application.surfaceContext.initialize(data)
        application.surfaceContext.setCallbacks(EventCallbacks.create())

        val surface = application.surfaceContext.surface
        data.surfaceConfiguration(surface)

        resolutionToCameraConverter = ResolutionToCameraConverter(surface.width, surface.height, data.viewWidth, data.viewHeight)
        surface.addOnChangedCallback { refreshView(it) }

        application.glContext.initialize(data)
        application.audioContext.initialize(data)

        view = StaticView(data.viewX, data.viewY,
                resolutionToCameraConverter.cameraWidthOut,
                resolutionToCameraConverter.cameraHeightOut)

        data.debugConfiguration(debugConfig)

        // TODO: BackendDebugConfiguration.set(debugConfig)

        cameras = Cameras(data, application.surfaceContext)

        sceneManager = SceneManager(data.deltaCalculator, data.startingScene ?: { null })

        if (Game.persistenceMap == null) {
            configuration.setPersistence()
        }
    }

    fun refreshView(surface: DrawSurface) {
        val converter = resolutionToCameraConverter

        converter.calculate(Game.world, surface)
        cameras.world.width = converter.cameraWidthOut

        converter.calculate(Game.ui, surface)
        cameras.ui.update(converter.cameraWidthOut, converter.cameraHeightOut)
    }

    inner class Cameras(data: ConfigurationData, surfaceContext: SurfaceContext) {
        val world: WorldCamera
        val ui: UICamera

        val matrices = GameMatrices(surfaceContext.surface, data.projectionMatrixConfiguration, view)

        init {
            val contentScale = surfaceContext.display.contentScale
            world = WorldCamera(view, matrices)
            ui = UICamera(matrices, world, vec(contentScale.xScale, contentScale.yScale))
        }
    }

}