package com.mechanica.engine.game.configuration

import com.mechanica.engine.display.Window
import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.MainScene
import com.mechanica.engine.scenes.scenes.LoadScene
import org.joml.Matrix4f

interface GameConfiguration {
    var initalize: Boolean
    fun setResolution(width: Int, height: Int)
    fun setFullscreen(isFullscreen: Boolean)
    fun setViewport(width: Double = 0.0, height: Double = 0.0)
    fun setViewLocation(x: Double, y: Double)

    fun setStartingState(scene: () -> MainScene)
    fun setLoader(loader: () -> LoadScene)

    fun setSaveData(vararg savedata: Any)

    fun setMultisampling(samples: Int)

    fun configureWindow(configuration: (Window.() -> Unit))
    fun configureDebugMode(configuration: GameDebugConfiguration.() -> Unit)

    fun configureProjectionMatrix(configuration: Matrix4f.(View) -> Unit)
}