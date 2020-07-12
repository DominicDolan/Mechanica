package com.mechanica.engine.game.configuration

import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.MainScene
import org.joml.Matrix4f

interface ConfigurationData {
    val monitor: Monitor?
    val title: String?
    val resolutionWidth: Int?
    val resolutionHeight: Int?
    val viewWidth: Double?
    val viewHeight: Double?
    val viewX: Double?
    val viewY: Double?
    val fullscreen: Boolean?
    val startingScene: (() -> MainScene)?
    val windowConfiguration: (Window.() -> Unit)?
    val debugConfiguration: (GameDebugConfiguration.() -> Unit)?
    val projectionMatrixConfiguration: (Matrix4f.(View) -> Unit)?

}