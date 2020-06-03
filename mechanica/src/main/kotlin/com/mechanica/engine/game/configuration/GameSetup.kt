package com.mechanica.engine.game.configuration

import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window
import com.mechanica.engine.debug.GameDebugConfiguration
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.game.view.ResolutionConverter
import com.mechanica.engine.game.view.View
import com.mechanica.engine.input.ControlsMap
import com.mechanica.engine.input.KeyboardHandler
import com.mechanica.engine.input.MouseHandler
import com.mechanica.engine.scenes.scenes.MainScene
import com.mechanica.engine.scenes.scenes.LoadScene
import org.joml.Matrix4f
import org.lwjgl.glfw.GLFW

class GameSetup(data: NullableConfigurationData) : ConfigurationData {
    override val monitor = Monitor.getPrimaryMonitor()
    
    override val title: String = data.title ?: "Mechanica"
    override val resolutionWidth: Int
    override val resolutionHeight: Int
    override val viewWidth: Double
    override val viewHeight: Double
    override val viewX: Double = data.viewX ?: 0.0
    override val viewY: Double = data.viewX ?: 0.0
    override val saveData: Array<Any> = data.saveData ?: emptyArray()
    override val controlsMap: ControlsMap = data.controlsMap ?: object : ControlsMap() { }
    override val fullscreen: Boolean = data.fullscreen ?: false
    override val startingScene: (() -> MainScene)? = data.startingScene
    override val loadState: (() -> LoadScene)? = data.loadState
    override val windowConfiguration: (Window.() -> Unit) = data.windowConfiguration ?: { }
    override val debugConfiguration: (GameDebugConfiguration.() -> Unit) = data.debugConfiguration ?: { }
    override val projectionMatrixConfiguration: (Matrix4f.(View) -> Unit)
            = data.projectionMatrixConfiguration ?: GameMatrices.Companion::defaultProjectionMatrix

    val ratio: Double

    val window: Window
    val debugConfig = GameDebugConfiguration()

    val resolutionConverter: ResolutionConverter

    init {

        val resolutionWasSet = data.resolutionWidth != null && data.resolutionHeight != null

        val resolution = setResolution(resolutionWasSet, data)
        resolutionWidth = resolution.first
        resolutionHeight = resolution.second

        ratio = resolutionHeight.toDouble()/resolutionWidth.toDouble()

        window = setWindow(resolutionWasSet)
        setCallbacks(window)

        resolutionConverter = ResolutionConverter(resolutionWidth, resolutionHeight, data.viewWidth, data.viewHeight)

        viewWidth = resolutionConverter.viewWidthOut
        viewHeight = resolutionConverter.viewHeightOut

        windowConfiguration(window)
        debugConfiguration(debugConfig)
    }

    private fun setWindow(resolutionWasSet: Boolean): Window {
        return if (fullscreen && !resolutionWasSet) {
            Window.create(title, monitor)
        } else if (fullscreen && resolutionWasSet) {
            Window.create(title, resolutionWidth, resolutionHeight, monitor)
        } else {
            Window.create(title, resolutionWidth, resolutionHeight).also { centerWindow(it) }
        }
    }

    private fun centerWindow(window: Window) {
        val monitor = Monitor.getPrimaryMonitor()
        val screenWidth = monitor.currentVideoMode.width()
        val screenHeight = monitor.currentVideoMode.height()
        window.position.set((screenWidth - window.width)/2, (screenHeight - window.height)/2)
    }

    private fun setCallbacks(window: Window) {
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.glfwSetKeyCallback(window.id, KeyboardHandler.KeyHandler())
        GLFW.glfwSetCharCallback(window.id, KeyboardHandler.TextHandler())

        GLFW.glfwSetMouseButtonCallback(window.id, MouseHandler.ButtonHandler())
        GLFW.glfwSetCursorPosCallback(window.id, MouseHandler.CursorHandler())
        GLFW.glfwSetScrollCallback(window.id, MouseHandler.ScrollHandler())

    }

    private fun setResolution(resolutionWasSet: Boolean, data: NullableConfigurationData): Pair<Int, Int> {
        val defaultResolutionWidth = (monitor.currentVideoMode.width()*0.75).toInt()
        val defaultResolutionHeight = (monitor.currentVideoMode.height()*0.75).toInt()

        return Pair(
                if (resolutionWasSet) data.resolutionWidth ?: defaultResolutionWidth
                else defaultResolutionWidth,
                if (resolutionWasSet) data.resolutionHeight ?: defaultResolutionHeight
                else defaultResolutionHeight
        )
    }
}