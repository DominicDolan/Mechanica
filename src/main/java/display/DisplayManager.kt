package display

import input.KeyboardHandler
import input.MouseHandler
import org.lwjgl.glfw.Callbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL13.GL_MULTISAMPLE
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil

/**
 * Created by domin on 28/10/2017.
 */
class DisplayManager(var width: Int, var height: Int, fullscreen: Boolean = false, borderless: Boolean = false, var refreshRate: Int = GLFW_DONT_CARE) {

    val ratio: Double get() =  width.toDouble()/height.toDouble()

    // The window handle
    var window: Long = 0

    init {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize  Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        // Configure GLFW
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable
        glfwWindowHint(GLFW_STENCIL_BITS, 8);
        glfwWindowHint(GLFW_SAMPLES, 8)

        val borderlessFullscreen = fullscreen && borderless
        val monitor = glfwGetPrimaryMonitor()
        val vidMode = glfwGetVideoMode(monitor)
        if (borderlessFullscreen && vidMode != null) {
            width = vidMode.width()
            height = vidMode.height()
            glfwWindowHint(GLFW_RED_BITS, vidMode.redBits())
            glfwWindowHint(GLFW_GREEN_BITS, vidMode.greenBits())
            glfwWindowHint(GLFW_BLUE_BITS, vidMode.blueBits())
            glfwWindowHint(GLFW_REFRESH_RATE, vidMode.refreshRate())
            refreshRate = vidMode.refreshRate()
        }
        // Create the window
        window = glfwCreateWindow(width, height, "Hello World!", if (fullscreen) monitor else MemoryUtil.NULL, MemoryUtil.NULL)
        if (fullscreen) {
//            glfwSetWindowMonitor(window, monitor, 0, 0, width, height, refreshRate)
        }
        if (window == MemoryUtil.NULL)
            throw RuntimeException("Failed to create the GLFW window")

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, KeyboardHandler())
        glfwSetMouseButtonCallback(window, MouseHandler.ButtonHandler())
        glfwSetCursorPosCallback(window, MouseHandler.CursorHandler())

        // Get the thread stack and push a new frame
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight)

            // Get the inverseRes of the primary monitor
            val vidmode = glfwGetVideoMode(monitor)

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode!!.width() - pWidth.get(0)) / 2,
                    (vidmode!!.height() - pHeight.get(0)) / 2
            )
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)

        GL.createCapabilities()
        glEnable(GL_MULTISAMPLE)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window)

        Game.ready = true
    }


    fun destroy(){
        // Free the window callbacks and destroy the window
        Callbacks.glfwFreeCallbacks(window)
        glfwDestroyWindow(window)

        // Terminate GLFW and free the error callback
        glfwTerminate()
        glfwSetErrorCallback(null)!!.free()
    }

}