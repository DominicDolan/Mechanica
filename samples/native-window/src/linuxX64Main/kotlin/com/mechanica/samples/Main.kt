@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.samples

import com.mechanica.engine.configuration.ContextConfigurationData
import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.display.Display
import com.mechanica.engine.display.DrawSurface
import com.mechanica.engine.input.KeyInput
import com.mechanica.nativebackend.NativeInputFactory
import com.mechanica.nativebackend.NativeOpenGLContext
import com.mechanica.nativebackend.NativeSurfaceContext
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*

fun main() {
    glfwSetErrorCallback(staticCFunction { code: Int, message: CPointer<ByteVar>? ->
        println("GLFW error $code: ${message?.toKString() ?: "unknown error"}")
    })
    val context = NativeSurfaceContext()
    val configuration = object : ContextConfigurationData {
        override val display: Display? = null
        override var title = "Mechanica Native OpenGL"
        override var resolutionWidth: Int? = 960
        override var resolutionHeight: Int? = 540
        override var viewWidth: Double? = null
        override var viewHeight: Double? = null
        override var viewX = 0.0
        override var viewY = 0.0
        override var fullscreen = false
        override var multisamplingSamples = 0
        override var surfaceConfiguration: DrawSurface.() -> Unit = {}
    }
    context.initialize(configuration)
    try {
        context.setCallbacks(EventCallbacks.create())
        val surface = context.surface
        surface.vSync = true
        val version = checkNotNull(glGetString(GL_VERSION.toUInt())) { "OpenGL context is unavailable" }
        println("OpenGL ${version.reinterpret<ByteVar>().toKString()}")
        println("Hold Space to change the background; press S for a screenshot, or Escape to exit.")
        val glContext = NativeOpenGLContext(context)
        glContext.initialize(configuration)
        try {
        checkNativeBuffers()
        val triangle = Triangle()
        try {
            val keys = NativeInputFactory().keyIds()
            var screenshotKeyHeld = false
            do {
                glViewport(0, 0, surface.width, surface.height)
                if (KeyInput.isPressed(keys.ESC.id)) surface.shouldClose()
                if (KeyInput.isPressed(keys.SPACE.id)) glClearColor(0.12f, 0.35f, 0.23f, 1f)
                else glClearColor(0.10f, 0.17f, 0.24f, 1f)
                glContext.startFrame()
                triangle.draw()
                val screenshotPressed = KeyInput.isPressed(keys.S.id)
                if (screenshotPressed && !screenshotKeyHeld) {
                    glContext.saveScreenshot("/tmp/mechanica-native-screenshot.png")
                    println("Screenshot saved to /tmp/mechanica-native-screenshot.png")
                }
                screenshotKeyHeld = screenshotPressed
            } while (surface.update())
        } finally { triangle.close() }
        } finally { glContext.destroy() }
    } finally { context.destroy() }
}
