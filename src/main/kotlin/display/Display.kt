package display

import input.KeyboardHandler
import input.MouseHandler
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.Platform
import java.nio.FloatBuffer

object Display : IDisplay {
    override val width: Int
        get() = setter.width
    override val height: Int
        get() = setter.height
    val ratio: Double
        get() = width.toDouble()/height.toDouble()
    override val fullscreen: Boolean
        get() = setter.fullscreen
    override val borderless: Boolean
        get() = setter.borderless
    override val refreshRate: Int
        get() = setter.refreshRate
    override val title: String
        get() = setter.title
    override val monitor: Long
        get() = setter.monitor
    override val window: Long
        get() = setter.window
    override val contentScaleX: Float
        get() = setter.contentScaleX
    override val contentScaleY: Float
        get() = setter.contentScaleY

    private val setter = DisplaySetter()

    internal fun init(
            width: Int, height: Int,
            fullscreen: Boolean = false,
            borderless: Boolean = false,
            refreshRate: Int = GLFW.GLFW_DONT_CARE,
            title: String = "Mechanica") {
        setter.width = width
        setter.height = height
        setter.refreshRate = refreshRate
        setter.title = title

        setter.initialize()
        setter.setWindowHints()
        setter.configureVidMode(fullscreen, borderless)

        setter.setContentScale()
        setter.createWindow()
        setter.setCallbacks()

        setter.pushNewFrame()

        setter.createContext()

        Game.ready = true
    }

    internal fun destroy(){
        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(window)
        GLFW.glfwDestroyWindow(window)

        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate()
        GLFW.glfwSetErrorCallback(null)!!.free()
    }

    private class DisplaySetter : IDisplay {
        override var width: Int = 0
        override var height: Int = 0
        override var fullscreen: Boolean = false
        override var borderless: Boolean = false
        override var refreshRate: Int = 0
        override var title: String = ""
        override var monitor: Long = 0
        override var window: Long = 0
        override var contentScaleX: Float = 1f
        override var contentScaleY: Float = 1f

        fun initialize() {
            // Setup an error callback. The default implementation
            // will print the error message in System.err.
            GLFWErrorCallback.createPrint(System.err).set()

            // Initialize  Most GLFW functions will not work before doing this.
            check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
        }

        fun setWindowHints() {
            // Configure GLFW
            GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden after creation
            GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE) // the window will be resizable
            GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 8)
            GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, 8)
        }

        fun configureVidMode(fullscreen: Boolean, borderless: Boolean) {
            this.fullscreen = fullscreen
            this.borderless = borderless

            val borderlessFullscreen = fullscreen && borderless
            monitor = GLFW.glfwGetPrimaryMonitor()
            val vidMode = GLFW.glfwGetVideoMode(monitor)
            if (borderlessFullscreen && vidMode != null) {
                width = vidMode.width()
                height = vidMode.height()
                GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, vidMode.redBits())
                GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, vidMode.greenBits())
                GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, vidMode.blueBits())
                GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, vidMode.refreshRate())
                refreshRate = vidMode.refreshRate()
            }
        }

        fun createWindow() {
            if (Platform.get() !== Platform.MACOSX) {
                width = Math.round(width * contentScaleX)
                height = Math.round(height * contentScaleY)
            }
            // Create the window
            window = GLFW.glfwCreateWindow(width, height, title, if (fullscreen) monitor else MemoryUtil.NULL, MemoryUtil.NULL)

            if (Display.window == MemoryUtil.NULL)
                throw RuntimeException("Failed to create the GLFW window")

        }

        fun setContentScale() {
            MemoryStack.stackPush().use {
                val px: FloatBuffer = it.mallocFloat(1)
                val py: FloatBuffer = it.mallocFloat(1)

                GLFW.glfwGetMonitorContentScale(monitor, px, py)

                contentScaleX = px[0]
                contentScaleY = py[0]
            }
        }

        fun setCallbacks() {
            // Setup a key callback. It will be called every time a key is pressed, repeated or released.
            GLFW.glfwSetKeyCallback(window, KeyboardHandler())
            GLFW.glfwSetMouseButtonCallback(window, MouseHandler.ButtonHandler())
            GLFW.glfwSetCursorPosCallback(window, MouseHandler.CursorHandler())
            GLFW.glfwSetScrollCallback(window, MouseHandler.ScrollHandler())

        }

        fun pushNewFrame() {
            // Get the thread stack and push a new frame
            MemoryStack.stackPush().use { stack ->
                val width = stack.mallocInt(1) // int*
                val height = stack.mallocInt(1) // int*

                // Get the window size passed to glfwCreateWindow
                GLFW.glfwGetWindowSize(window, width, height)

                // Get the inverseRes of the primary monitor
                val vidmode = GLFW.glfwGetVideoMode(monitor)

                if (vidmode != null) {
                    // Center the window
                    GLFW.glfwSetWindowPos(
                            window,
                            (vidmode.width() - width.get(0)) / 2,
                            (vidmode.height() - height.get(0)) / 2
                    )
                }
            } // the stack frame is popped automatically

        }

        fun createContext() {
            // Make the OpenGL context current
            GLFW.glfwMakeContextCurrent(window)

            GL.createCapabilities()
            GL11.glEnable(GL13.GL_MULTISAMPLE)
            // Enable v-sync
            GLFW.glfwSwapInterval(1)

            // Make the window visible
            GLFW.glfwShowWindow(window)

        }
    }
}