package com.mechanica.engine.game.configuration

import com.cave.library.matrix.mat4.Matrix4
import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.display.DrawSurface
import com.mechanica.engine.game.delta.DeltaCalculator
import com.mechanica.engine.game.view.View
import com.mechanica.engine.persistence.JsonFileStorer
import com.mechanica.engine.persistence.JsonStorer
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.util.getCallingClass

interface GameConfiguration {
    var initalize: Boolean
    fun setResolution(width: Int, height: Int)
    fun setFullscreen(isFullscreen: Boolean)
    fun setViewport(width: Double = 0.0, height: Double = 0.0)
    fun setViewLocation(x: Double, y: Double)

    fun setPersistence(jsonStorer: JsonStorer = JsonFileStorer(getCallingClass().packageName + "/res/data/persistence.json"))

    fun setStartingScene(scene: () -> Scene)

    fun setMultisampling(samples: Int)

    fun configureDrawSurface(configuration: (DrawSurface.() -> Unit))
    fun configureDebugMode(configuration: GameDebugConfiguration.() -> Unit)

    fun configureProjectionMatrix(configuration: Matrix4.(View) -> Unit)

    fun setDeltaTimeCalculator(calculator: DeltaCalculator)
}