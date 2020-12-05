package com.mechanica.engine.display

import com.mechanica.engine.context.GLContextOld
import com.mechanica.engine.context.GLFWContextOld
import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.resources.Resource
import com.mechanica.engine.utils.ImageData
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWImage
import org.lwjgl.system.MemoryUtil
import java.nio.ByteBuffer


class GLFWWindow private constructor(width: Int, height: Int, override val title: String, display: Display?, sharedWith: DesktopWindow?) : DesktopWindow {
    override val id: Long
    var hasInitialized = false
        private set

    override var isFocused: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_FOCUSED) == GLFW_TRUE
        set(value) { if (value) glfwFocusWindow(id) }

    override var isIconified: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_ICONIFIED ) == GLFW_TRUE
        set(value) {
            if (value) glfwIconifyWindow(id)
            else glfwRestoreWindow(id) }
    override var isMaximized: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_MAXIMIZED ) == GLFW_TRUE
        set(value) {
            if (value) glfwMaximizeWindow(id)
            else glfwRestoreWindow(id) }

    override val isHovered: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_HOVERED ) == GLFW_TRUE
    override var isVisible: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_VISIBLE ) == GLFW_TRUE
        set(value) {
            if (value) glfwShowWindow(id)
            else glfwHideWindow(id) }
    override var isResizable: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_RESIZABLE ) == 1
        set(value) { glfwSetWindowAttrib(id, GLFW_RESIZABLE, if (value) GLFW_TRUE else GLFW_FALSE ) }
    override var isDecorated: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_DECORATED ) == 1
        set(value) { glfwSetWindowAttrib(id, GLFW_DECORATED, if (value) GLFW_TRUE else GLFW_FALSE ) }
    override var isFloating: Boolean
        get() = glfwGetWindowAttrib(id, GLFW_FLOATING ) == 1
        set(value) { glfwSetWindowAttrib(id, GLFW_FLOATING, if (value) GLFW_TRUE else GLFW_FALSE ) }
    override var shouldClose: Boolean
        get() = glfwWindowShouldClose(id)
        set(value) { glfwSetWindowShouldClose(id, value) }
    override var vSync: Boolean = true
        set(value) {
            if (GLContextOld.initialized) {
                glfwSwapInterval(if (value) 1 else 0)
            }
            field = value
        }
    override val isFullscreen: Boolean
        get() = display != null

    private var finished = false

    override var opacity: Float
        get() = glfwGetWindowOpacity(id)
        set(value) = glfwSetWindowOpacity(id, value)

    var display: Display? = display
        get() {
            val monitor = field
            val monitorId = glfwGetWindowMonitor(id)
            val foundMonitor = GLFWMonitor.allMonitors.firstOrNull { it.id == monitorId }
            return if (monitorId == MemoryUtil.NULL){
                field = null
                null
            } else if (monitor != null && monitorId == monitor.id) {
                monitor
            } else foundMonitor?.also { field = it }
        }
        set(value) {
            if (value != null && value != field) {
                setFullscreen(value)
            } else if (value != field) {
                exitFullscreen()
            }
            field = value
        }

    override val position: PositionImpl = PositionImpl()

    override val resolution: Resolution by lazy {
        setResolution(GLFWResolution(0.px, 0.px, false))
    }

    override val size: DesktopWindow.Dimension by lazy {
        setWindowSize(DimensionImpl(0, 0, false))
    }

    private val callbackList = ArrayList<((DesktopWindow) -> Unit)>()

    init {
        id = glfwCreateWindow(width, height, title, display?.id ?: MemoryUtil.NULL, sharedWith?.id ?: MemoryUtil.NULL)

        if (id == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }
        hasInitialized = true

        setFramebufferSizeCallback()
        setWindowSizeCallback()
        glfwSetWindowRefreshCallback(id) {
            if (it == id) {
                for (callback in callbackList) {
                    callback(this)
                }
            }
        }
        glfwSetWindowCloseCallback(id) {
            finished = true
        }

    }

    override fun addOnChangedCallback(callback: (DesktopWindow) -> Unit) {
        callbackList.add(callback)
    }

    private fun setFramebufferSizeCallback() {
        glfwSetFramebufferSizeCallback(id) { window, w, h ->
            if (window == id) {
                val resolution = (resolution as GLFWResolution)
                resolution.width = w.px
                resolution.height = h.px
                resolution.isChanging = true

            }
        }
    }

    private fun setWindowSizeCallback() {
        glfwSetWindowSizeCallback(id) { window, w, h ->
            if (window == id) {
                val size = (size as DimensionImpl)
                size.width = w
                size.height = h
                size.isChanging = true
            }
        }
    }

    override fun update(): Boolean {
        swapBuffers()
        EventCallbacks.prepare()
        pollEvents()
        val shouldClose = this.shouldClose
        if (finished) {
            destroy()
        }
        return !shouldClose
    }

    override fun destroy() {
        if (hasInitialized) {
            Callbacks.glfwFreeCallbacks(id)
            glfwDestroyWindow(id)
            hasInitialized = false
        }
    }

    override fun shouldClose() {
        shouldClose = true
    }

    fun swapBuffers() {
        glfwSwapBuffers(id)
    }

    fun pollEvents() {
        glfwPollEvents()
    }

    override fun requestAttention() {
        glfwRequestWindowAttention(id)
    }

    private fun setWindowSize(out: DimensionImpl): DesktopWindow.Dimension {
        val widthArray = IntArray(1)
        val heightArray = IntArray(1)
        glfwGetWindowSize(id, widthArray, heightArray)
        out.width = widthArray[0]
        out.height = heightArray[0]
        return out
    }

    private fun setResolution(out: GLFWResolution): Resolution {
        val widthArray = IntArray(1)
        val heightArray = IntArray(1)
        glfwGetFramebufferSize(id, widthArray, heightArray)
        out.width = widthArray[0].px
        out.height = heightArray[0].px
        return out
    }

    override fun setIcon(resource: Resource) {
        val image = ImageData(resource)

        if (image.data != null) {
            setIcon(image.width, image.height, image.data)
        }

        image.free()
    }

    override fun setIcon(width: Int, height: Int, imageBuffer: ByteBuffer) {
        val image: GLFWImage = GLFWImage.malloc()
        val imagebf: GLFWImage.Buffer = GLFWImage.malloc(1)

        image.set(width, height, imageBuffer)
        imagebf.put(0, image)

        glfwSetWindowIcon(id, imagebf)
    }

    override fun setFullscreen() {
        setFullscreen(Display.getPrimaryMonitor())
    }

    override fun setFullscreen(display: Display) {
        val vSync = vSync
        val vidMode = if (display is GLFWMonitor) display.currentVideoMode else throw IllegalStateException("Unable to put window into fullscreen mode")
        glfwSetWindowMonitor(id, display.id, 0, 0, vidMode.width(), vidMode.height(), vidMode.refreshRate())
        this.vSync = vSync
    }

    override fun setFullscreen(display: Display, width: Int, height: Int, refreshRate: Int) {
        val vSync = vSync
        glfwSetWindowMonitor(id, display.id, 0, 0, width, height, refreshRate)
        this.vSync = vSync
    }

    override fun exitFullscreen(width: Int, height: Int) {
        if (this.display != null) {
            val vidMode = GLFWMonitor.getPrimaryMonitor().currentVideoMode
            val screenWidth = vidMode.width()
            val screenHeight = vidMode.height()

            if (width < 0 || height < 0) {
                setDefaultWindowDimensions(screenWidth, screenHeight)
            } else {
                val x = screenWidth - width/2
                val y = screenHeight - height/2
                glfwSetWindowMonitor(id, MemoryUtil.NULL, x, y, width, height, vidMode.refreshRate())

            }
        }
    }

    private fun setDefaultWindowDimensions(screenWidth: Int, screenHeight: Int) {
        val windowWidth = (0.75*screenWidth).toInt()
        val windowHeight = (0.75*screenHeight).toInt()
        val x = screenWidth/2 - windowWidth/2
        val y = screenHeight/2 - windowHeight/2
        glfwSetWindowMonitor(id, MemoryUtil.NULL, x, y, windowWidth, windowHeight, 0)

    }

    inner class PositionImpl : DesktopWindow.Position {
        val xArray = IntArray(1)
        val yArray = IntArray(1)
        override var x: Int
            set(value) = set(value, y)
            get() {
                updatePosition()
                return xArray[0]
            }
        override var y: Int
            set(value) = set(x, value)
            get() {
                updatePosition()
                return yArray[0]
            }

        private fun updatePosition() {
            glfwGetWindowPos(id, xArray, yArray)
        }

        override fun set(x: Int, y: Int) {
            glfwSetWindowPos(id, x, y)
        }
    }

    private data class GLFWResolution(
            override var width: Pixel,
            override var height: Pixel,
            var isChanging: Boolean) : Resolution

    private data class DimensionImpl(
            override var width: Int, 
            override var height: Int, 
            var isChanging: Boolean) : DesktopWindow.Dimension

    companion object {
        fun create(title: String, width: Int, height: Int, sharedWith: DesktopWindow? = null): GLFWWindow {
            GLFWContextOld.initialize()
            return GLFWWindow(width, height, title, null, sharedWith)
        }

        fun create(title: String, display: Display, sharedWith: DesktopWindow? = null): GLFWWindow {
            GLFWContextOld.initialize()
            val vidMode = if (display is GLFWMonitor) display.currentVideoMode else throw IllegalStateException("Unable to create window")
            val width = vidMode.width()
            val height = vidMode.height()
            glfwWindowHint(GLFW_RED_BITS, vidMode.redBits())
            glfwWindowHint(GLFW_GREEN_BITS, vidMode.greenBits())
            glfwWindowHint(GLFW_BLUE_BITS, vidMode.blueBits())
            glfwWindowHint(GLFW_REFRESH_RATE, vidMode.refreshRate())

            return GLFWWindow(width, height, title, display, sharedWith)
        }

        fun create(title: String, width: Int, height: Int, display: Display, sharedWith: DesktopWindow? = null): GLFWWindow {
            GLFWContextOld.initialize()
            return GLFWWindow(width, height, title, display, sharedWith)
        }

    }
}