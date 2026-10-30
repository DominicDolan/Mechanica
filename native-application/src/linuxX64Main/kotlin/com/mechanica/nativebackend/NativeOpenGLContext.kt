@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.configuration.ContextConfigurationData
import com.mechanica.engine.context.OpenGLContext
import com.mechanica.engine.context.SurfaceContext
import com.mechanica.nativebackend.stb.stbi_write_png
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*

class NativeOpenGLContext(private val surfaceContext: SurfaceContext) : OpenGLContext {
    private var vao = 0u
    override var majorVersion = 0
        private set
    override var minorVersion = 0
        private set

    override fun initialize(data: ContextConfigurationData) {
        memScoped {
            val major = alloc<IntVar>(); val minor = alloc<IntVar>(); val array = alloc<UIntVar>()
            glGetIntegerv(GL_MAJOR_VERSION.toUInt(), major.ptr)
            glGetIntegerv(GL_MINOR_VERSION.toUInt(), minor.ptr)
            majorVersion = major.value; minorVersion = minor.value
            check(majorVersion > 4 || majorVersion == 4 && minorVersion >= 3) { "OpenGL 4.3 is required" }
            glGenVertexArrays(1, array.ptr)
            vao = array.value
        }
        check(vao != 0u) { "Unable to create OpenGL vertex array" }
        glBindVertexArray(vao)
        if (data.multisamplingSamples > 0) glEnable(GL_MULTISAMPLE.toUInt())
        else glDisable(GL_MULTISAMPLE.toUInt())
        glEnable(GL_BLEND.toUInt())
        glBlendFunc(GL_SRC_ALPHA.toUInt(), GL_ONE_MINUS_SRC_ALPHA.toUInt())
        glClearColor(0f, 0f, 0f, 0f)
        val surface = surfaceContext.surface
        glViewport(0, 0, surface.width, surface.height)
        surface.addOnChangedCallback { glViewport(0, 0, it.width, it.height) }
        glfwSwapInterval(if (surface.vSync) 1 else 0)
    }

    override fun startFrame() {
        glEnable(GL_STENCIL_TEST.toUInt())
        glStencilMask(0xffu)
        glClearStencil(0)
        glClear((GL_COLOR_BUFFER_BIT or GL_STENCIL_BUFFER_BIT).toUInt())
        glDisable(GL_STENCIL_TEST.toUInt())
    }

    override fun saveScreenshot(path: String) {
        val surface = surfaceContext.surface
        val width = surface.width; val height = surface.height
        require(width > 0 && height > 0 && width.toLong() * height * 4 <= Int.MAX_VALUE) { "Invalid screenshot dimensions" }
        val row = width * 4
        val raw = ByteArray(row * height)
        val pixels = ByteArray(raw.size)
        memScoped {
            val alignment = alloc<IntVar>()
            glGetIntegerv(GL_PACK_ALIGNMENT.toUInt(), alignment.ptr)
            try {
                glPixelStorei(GL_PACK_ALIGNMENT.toUInt(), 1)
                glReadBuffer(GL_BACK.toUInt())
                raw.usePinned { glReadPixels(0, 0, width, height, GL_RGBA.toUInt(), GL_UNSIGNED_BYTE.toUInt(), it.addressOf(0)) }
            } finally { glPixelStorei(GL_PACK_ALIGNMENT.toUInt(), alignment.value) }
        }
        for (y in 0 until height) raw.copyInto(pixels, y * row, (height - y - 1) * row, (height - y) * row)
        ensureParent(path)
        pixels.usePinned {
            check(stbi_write_png(path, width, height, 4, it.addressOf(0), row) != 0) { "Unable to write screenshot: $path" }
        }
    }

    override fun destroy() {
        if (vao != 0u && glfwGetCurrentContext() != null) memScoped {
            val array = alloc<UIntVar>(); array.value = vao
            glDeleteVertexArrays(1, array.ptr)
        }
        vao = 0u
    }
}
