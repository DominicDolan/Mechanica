package com.mechanica.engine.context

import com.mechanica.engine.configuration.ContextConfigurationData
import com.mechanica.engine.utils.enableAlphaBlending
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.*

class GL40Context(private val application: Application) : OpenGLContext {

    private var parsedVersionString: VersionStringParser? = null
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

    private fun parseVersionString(): VersionStringParser {
        val versionString: String = GL11.glGetString(GL11.GL_VERSION) ?:
        throw IllegalStateException("Unable to get the version of OpenGL")
        return VersionStringParser(versionString)
    }

    companion object {
        private const val uninitializedPropertyAccessMessage =
                "The OpenGL context has not yet been initialized. initialize(ContextConfigurationData) has to be called first"

        fun uninitializedException() = UninitializedPropertyAccessException(uninitializedPropertyAccessMessage)
    }
}