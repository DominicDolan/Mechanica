package com.mechanica.engine.context

import com.mechanica.engine.configuration.ContextConfigurationData
import com.mechanica.engine.utils.enableAlphaBlending
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.*
import java.nio.ByteBuffer
import java.awt.image.BufferedImage

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
        GL11.glClearColor(0f, 0f, 0f, 0f)

        GL11.glEnable(GL11.GL_STENCIL_TEST)

        enableAlphaBlending()
    }

    override fun startFrame() {
        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL11.GL_STENCIL_BUFFER_BIT)
    }

    override fun screenshot(): BufferedImage {
        // Clear with transparency for the screenshot
//        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL11.GL_STENCIL_BUFFER_BIT)
//        GL20.glClear(GL20.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val width = application.surfaceContext.surface.width
        val height = application.surfaceContext.surface.height

        val buffer: ByteBuffer = BufferUtils.createByteBuffer(width * height * 4)
        // Read the pixels from the framebuffer
        GL11.glReadBuffer(GL11.GL_BACK)
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer)
        // Create a ByteBuffer to store the pixel data
        // Create a BufferedImage to store the data
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

        // Convert the ByteBuffer data to BufferedImage
        for (x in 0 until width) {
            for (y in 0 until height) {
                val i = (x + (height - y - 1) * width) * 4
                val r = buffer.get(i).toInt() and 0xFF
                val g = buffer.get(i + 1).toInt() and 0xFF
                val b = buffer.get(i + 2).toInt() and 0xFF
                val a = buffer.get(i + 3).toInt() and 0xFF

                image.setRGB(x, y, (a shl 24) or (r shl 16) or (g shl 8) or b)
            }
        }

        return image
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