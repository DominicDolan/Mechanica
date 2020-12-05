package com.mechanica.engine.context

import com.mechanica.engine.configuration.ContextConfigurationData
import com.mechanica.engine.utils.enableAlphaBlending
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.*

class GL40Context(private val application: Application) : OpenGLContext {

    private var parsedVersionString: GLVersionStringParser? = null
    override val majorVersion: Int
        get() = parsedVersionString?.majorVersion ?: throw uninitializedException()
    override val minorVersion: Int
        get() = parsedVersionString?.minorVersion ?: throw uninitializedException()
    override val version: String
        get() = parsedVersionString?.version?.toString() ?: throw uninitializedException()

    override fun initialize(data: ContextConfigurationData) {
        GL.createCapabilities()
        parsedVersionString = parseVersionString()

        addWindowChangedListener()

        enableMultisampling(data.multisamplingSamples)
        GLFW.glfwSwapInterval(if (application.surfaceContext.surface.vSync) 1 else 0)

        bindVAO()

        GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        GL11.glEnable(GL11.GL_STENCIL_TEST)

        enableAlphaBlending()

        // TODO: Move this to its own context
        ALContext.initialize()
    }

    override fun startFrame() {
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL11.GL_STENCIL_BUFFER_BIT)
    }

    override fun destroy() {
        GL.setCapabilities(null)
    }

    private fun addWindowChangedListener() {
        application.surfaceContext.surface.addOnChangedCallback {
            GL11.glViewport(0, 0, it.width, it.height)
        }
    }

    private fun enableMultisampling(samples: Int) {
        if (samples != 0) {
            GL11.glEnable(GL13.GL_MULTISAMPLE)
        } else {
            GL11.glDisable(GL13.GL_MULTISAMPLE)
        }
    }

    private fun bindVAO() {
        val vao = GL30.glGenVertexArrays()
        GL30.glBindVertexArray(vao)
    }

    private fun parseVersionString(): GLVersionStringParser {
        val versionString: String = GL11.glGetString(GL11.GL_VERSION) ?:
        throw IllegalStateException("Unable to get the version of OpenGL")
        return GLVersionStringParser(versionString)
    }

    private class GLVersionStringParser(versionString: String) {
        val majorVersion: Int
        val minorVersion: Int
        val version: Double

        init {
            val majorVersionIndex = versionString.indexOf('.')
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

    companion object {
        private const val uninitializedPropertyAccessMessage =
                "The OpenGL context has not yet been initialized. initialize(ContextConfigurationData) has to be called first"

        fun uninitializedException() = UninitializedPropertyAccessException(uninitializedPropertyAccessMessage)
    }
}