package com.mechanica.engine.context

import com.mechanica.engine.configuration.ContextConfigurationData
import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.display.*
import com.mechanica.engine.input.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL

class GLFWContext() : SurfaceContext {

    private var major: Int? = null
    override val majorVersion: Int
        get() = major ?: throw uninitializedException()
    private var minor: Int? = null
    override val minorVersion: Int
        get() = minor ?: throw uninitializedException()
    override val version: String
        get() = super.version

    private var monitor: GLFWMonitor? = null
    override val display: Display
        get() = monitor ?: throw uninitializedException()
    private var window: GLFWWindow? = null
    override val surface: DrawSurface
        get() = window ?: throw uninitializedException()

    private constructor(newWindow: GLFWWindow, other: GLFWContext) : this() {
        major = other.major
        minor = other.minor
        monitor = other.monitor
        window = newWindow
    }

    override fun initialize(data: ContextConfigurationData) {
        createContext(data.multisamplingSamples)
        setVersions()

        monitor = data.display as? GLFWMonitor ?: GLFWMonitor(GLFW.glfwGetPrimaryMonitor())

        val window = setupWindow(data)
        GLFW.glfwMakeContextCurrent(window.id)

    }

    private fun createContext(samples: Int) {
        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }

        if (samples != 0) {
            GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, samples)
        }

        GLFWErrorCallback.createPrint(System.err).set()

    }

    private fun setVersions() {

        val majorArray = IntArray(1)
        val minorArray = IntArray(1)
        val revisionArray = IntArray(1)

        GLFW.glfwGetVersion(majorArray, minorArray, revisionArray)

        major = majorArray[0]
        minor = minorArray[0]
    }

    override fun createSharedContext(): SurfaceContext {
        val thisWindow = window

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE)

        val newWindow = if (thisWindow != null) {
            GLFWWindow.create(thisWindow.title, 640, 480, thisWindow)
        } else throw uninitializedException()


        return GLFWContext(newWindow, this)
    }

    override fun activate() {
        val id = window?.id
        if (id != null && GLFW.glfwGetCurrentContext() != id) {
            GLFW.glfwMakeContextCurrent(id)
            GL.createCapabilities()
        }
    }

    override fun setCallbacks(callbacks: EventCallbacks) {
        val window = this.window
        if (window != null) {
            GLFW.glfwSetKeyCallback(window.id, GLFWKeyHandler(callbacks.keyboardHandler))
            GLFW.glfwSetCharCallback(window.id, GLFWTextInputHandler(callbacks.keyboardHandler))

            GLFW.glfwSetMouseButtonCallback(window.id, GLFWMouseButtonHandler(callbacks.mouseHandler))
            GLFW.glfwSetCursorPosCallback(window.id, GLFWMouseCursorHandler(callbacks.mouseHandler))
            GLFW.glfwSetScrollCallback(window.id, GLFWScrollHandler(callbacks.mouseHandler))
        } else throw uninitializedException()
    }

    override fun destroy() {
        GLFW.glfwSetErrorCallback(null)?.free()
        GLFW.glfwTerminate()
    }

    private fun setupWindow(data: ContextConfigurationData): DesktopWindow {
        val fullscreen = data.fullscreen
        val width = data.resolutionWidth
        val height = data.resolutionHeight

        val window = if (fullscreen && (width == null || height == null)) {
            GLFWWindow.create(data.title, display)
        } else if(width != null && height != null) {
            if (fullscreen) {
                GLFWWindow.create(data.title, width, height, display)
            } else {
                GLFWWindow.create(data.title, width, height)
            }
        } else GLFWWindow.create(data.title, (0.75*display.width).toInt(), (0.75*display.height).toInt())

        if (!fullscreen) centerWindow(window)

        this.window = window

        return window
    }

    private fun centerWindow(window: DesktopWindow) {
        val screenWidth = display.width
        val screenHeight = display.height
        window.position.set((screenWidth - window.width)/2, (screenHeight - window.height)/2)
    }


    companion object {
        private const val uninitializedPropertyAccessMessage =
                "The surface context has not yet been initialized. initialize(ContextConfigurationData) has to be called first"

        fun uninitializedException() = UninitializedPropertyAccessException(uninitializedPropertyAccessMessage)
    }

}