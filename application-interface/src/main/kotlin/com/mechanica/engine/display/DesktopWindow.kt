package com.mechanica.engine.display

import com.mechanica.engine.resources.Resource
import java.nio.ByteBuffer

interface DesktopWindow : DrawSurface {
    val title: String
    val id: Long
    var isFocused: Boolean
    var isIconified: Boolean
    var isMaximized: Boolean
    val isHovered: Boolean
    var isVisible: Boolean
    var isResizable: Boolean
    var isDecorated: Boolean
    var isFloating: Boolean
    var opacity: Float
    val position: Position
    val size: Dimension
    var shouldClose: Boolean
    val isFullscreen: Boolean

    fun requestAttention()
    fun setIcon(resource: Resource)
    fun setIcon(width: Int, height: Int, imageBuffer: ByteBuffer)
    fun setFullscreen()
    fun setFullscreen(display: Display)
    fun setFullscreen(display: Display, width: Int, height: Int, refreshRate: Int = 60)
    fun exitFullscreen(width: Int = -1, height: Int = -1)

    interface Dimension {
        val width: Int
        val height: Int
    }

    interface Position {
        var x: Int
        var y: Int

        fun set(x: Int, y: Int) {
            this.x = x
            this.y = y
        }
    }

}