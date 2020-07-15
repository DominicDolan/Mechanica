package com.mechanica.engine.display

import com.mechanica.engine.context.loader.DisplayLoader
import com.mechanica.engine.resources.Resource
import java.nio.ByteBuffer

interface Window {
    val title: String
    val id: Long
    val width: Int
    val height: Int
    val aspectRatio: Double
    var isFocused: Boolean
    var isIconified: Boolean
    var isMaximized: Boolean
    val isHovered: Boolean
    var isVisible: Boolean
    var isResizable: Boolean
    var isDecorated: Boolean
    var isFloating: Boolean
    var vSync: Boolean
    var opacity: Float
    val position: Position
    val resolution: Dimension
    val size: Dimension
    val isResizing: Boolean
    var shouldClose: Boolean
    val isFullscreen: Boolean

    fun addRefreshCallback(callback: (Window) -> Unit)
    fun requestAttention()
    fun setIcon(resource: Resource)
    fun setIcon(width: Int, height: Int, imageBuffer: ByteBuffer)
    fun setFullscreen()
    fun setFullscreen(monitor: Monitor)
    fun setFullscreen(monitor: Monitor, width: Int, height: Int, refreshRate: Int = 60)
    fun exitFullscreen(width: Int = -1, height: Int = -1)
    fun destroy()
    fun update(): Boolean

    interface Dimension {
        val width: Int
        val height: Int
    }

    interface Position {
        var x: Int
        var y: Int

        fun set(x: Int, y: Int)
    }

    companion object {
        fun create(title: String, width: Int, height: Int): Window {
            return DisplayLoader.createWindow(title, width, height)
        }

        fun create(title: String, monitor: Monitor): Window {
            return DisplayLoader.createWindow(title, monitor)
        }

        fun create(title: String, width: Int, height: Int, monitor: Monitor): Window {
            return DisplayLoader.createWindow(title, width, height, monitor)
        }
    }
}