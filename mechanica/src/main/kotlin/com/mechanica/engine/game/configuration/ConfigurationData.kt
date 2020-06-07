package com.mechanica.engine.game.configuration

import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window
import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.MainScene
import com.mechanica.engine.scenes.scenes.LoadScene
import org.joml.Matrix4f
import com.mechanica.engine.state.LoadState
import com.mechanica.engine.state.State

interface ConfigurationData {
    val monitor: Monitor?
    val title: String?
    val resolutionWidth: Int?
    val resolutionHeight: Int?
    val viewWidth: Double?
    val viewHeight: Double?
    val viewX: Double?
    val viewY: Double?
    val saveData: Array<Any>?
    val fullscreen: Boolean?
    val startingScene: (() -> MainScene)?
    val loadState: (() -> LoadScene)?
    val windowConfiguration: (Window.() -> Unit)?
    val debugConfiguration: (GameDebugConfiguration.() -> Unit)?
    val projectionMatrixConfiguration: (Matrix4f.(View) -> Unit)?

    companion object {
        fun emptyState(): State {
            return object : State(){
                override fun update(delta: Double) {}
                override fun render(draw: Drawer) {}
            }
        }

        fun emptyLoadeState(): LoadState {
            return object : LoadState(){
                override fun preLoad() {}
                override fun renderLoadScreen(draw: Drawer) {}
                override fun load() { }
            }
        }
    }
}