package com.mechanica.engine.context

import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.display.Window
import com.mechanica.engine.input.*
import com.mechanica.engine.utils.enableAlphaBlending
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.*
import java.util.*


object GLContext : Version {
    private val extensionMap by lazy { createExtensionsMap() }

    private val errorMessage = "Version and extension information is not available yet." +
            "\nGLContext.initialize() must be called before any version information is available"
    private var parsedVersionString: GLVersionStringParser? = null
    override val majorVersion: Int
        get() = parsedVersionString?.majorVersion ?: throw IllegalStateException(errorMessage)
    override val minorVersion: Int
        get() = parsedVersionString?.minorVersion ?: throw IllegalStateException(errorMessage)
    override val version: Double
        get() = parsedVersionString?.version ?: throw IllegalStateException(errorMessage)

    var initialized = false
        private set

    fun isExtensionSupported(extension: String): Boolean {
        return extensionMap.containsKey(extension)
    }

    val supportedExtensions: Collection<String>
        get() = extensionMap.keys

    fun initialize(window: Window) {
        initContext(window.id)
        parsedVersionString = parseVersionString()

        window.addRefreshCallback {
            GL11.glViewport(0, 0, it.width, it.height)
        }

        GLFW.glfwSwapInterval(if (window.vSync) 1 else 0)

        multisampling()

        bindVAO()

        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glEnable(GL11.GL_STENCIL_TEST)

        enableAlphaBlending()
        initialized = true
    }

    private fun initContext(windowId: Long) {
        GLFW.glfwMakeContextCurrent(windowId)
        GL.createCapabilities()
    }


    fun setCallbacks(window: Window, callbacks: EventCallbacks) {
        GLFW.glfwSetKeyCallback(window.id, GLFWKeyHandler(callbacks.keyboardHandler))
        GLFW.glfwSetCharCallback(window.id, GLFWTextInputHandler(callbacks.keyboardHandler))

        GLFW.glfwSetMouseButtonCallback(window.id, GLFWMouseButtonHandler(callbacks.mouseHandler))
        GLFW.glfwSetCursorPosCallback(window.id, GLFWMouseCursorHandler(callbacks.mouseHandler))
        GLFW.glfwSetScrollCallback(window.id, GLFWScrollHandler(callbacks.mouseHandler))

    }

    private fun parseVersionString(): GLVersionStringParser {
        val versionString: String = GL11.glGetString(GL11.GL_VERSION) ?:
        throw IllegalStateException("Unable to get the version of OpenGL")
        return GLVersionStringParser(versionString)
    }

    private fun bindVAO() {
        val vao = GL30.glGenVertexArrays()
        GL30.glBindVertexArray(vao)
    }

    private fun multisampling() {
        if (GLFWContext.samples != 0) {
            GL11.glEnable(GL13.GL_MULTISAMPLE)
        } else {
            GL11.glDisable(GL13.GL_MULTISAMPLE)
        }
    }

    fun startFrame() {
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL11.GL_STENCIL_BUFFER_BIT)
    }

    fun free() {
        GL.setCapabilities(null)
    }

    private fun createExtensionsMap(): MutableMap<String, Boolean> {
        val supportedExtensions = getSupportedExtensions()

        val extensionMap = HashMap<String, Boolean>()
        for (extension in supportedExtensions) {
            extensionMap[extension] = true
        }
        return extensionMap
    }

    private fun getSupportedExtensions(): Array<String> {
        return if (majorVersion >= 3) {
            val numExtensions = GL11.glGetInteger(GL30.GL_NUM_EXTENSIONS)
            Array(numExtensions) { GL30.glGetStringi(GL11.GL_EXTENSIONS, it) ?: "NULL" }
        } else {
            val extensionsAsString = GL11.glGetString(GL11.GL_EXTENSIONS)
            extensionsAsString!!.split(" ").toTypedArray()
        }
    }

    private class GLVersionStringParser(versionString: String) {
        val majorVersion: Int
        val minorVersion: Int
        val version: Double

        init {val majorVersionIndex = versionString.indexOf('.')
            var minorVersionIndex = majorVersionIndex + 1
            while (minorVersionIndex < versionString.length && Character.isDigit(minorVersionIndex)) {
                minorVersionIndex++
            }
            minorVersionIndex++
            majorVersion = versionString.substring(0, majorVersionIndex).toInt()
            minorVersion = versionString.substring(majorVersionIndex + 1, minorVersionIndex).toInt()
            version = versionString.substring(0, minorVersionIndex).toDouble()

        }
    }
}