@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import cnames.structs.GLFWmonitor
import cnames.structs.GLFWwindow
import com.mechanica.engine.configuration.ContextConfigurationData
import com.mechanica.engine.context.SurfaceContext
import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.display.*
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*

/** Linux GLFW surface and current OpenGL context. Call on the GLFW event thread. */
class NativeSurfaceContext private constructor(
    private val ownsGlfw: Boolean,
    private var nativeWindow: NativeSurface?,
    private var nativeDisplay: NativeDisplay?,
) : SurfaceContext {
    constructor() : this(true, null, null)
    private var major = 0
    private var minor = 0
    private var input: NativeInput? = null
    override val majorVersion: Int get() = major
    override val minorVersion: Int get() = minor
    override val display: Display get() = checkNotNull(nativeDisplay) { "Surface not initialized" }
    override val surface: DrawSurface get() = checkNotNull(nativeWindow) { "Surface not initialized" }

    override fun initialize(data: ContextConfigurationData) {
        check(ownsGlfw && nativeWindow == null) { "Surface already initialized" }
        check(glfwInit() == GLFW_TRUE) { "Unable to initialize GLFW" }
        try {
            memScoped {
                val a = alloc<IntVar>(); val b = alloc<IntVar>(); val c = alloc<IntVar>()
                glfwGetVersion(a.ptr, b.ptr, c.ptr)
                major = a.value; minor = b.value
            }
            val display = if (data.display == null) NativeDisplay(checkNotNull(glfwGetPrimaryMonitor()))
                else data.display as? NativeDisplay ?: error("Display belongs to another backend")
            nativeDisplay = display
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3)
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
            glfwWindowHint(GLFW_SAMPLES, data.multisamplingSamples)
            val width = data.resolutionWidth ?: if (data.fullscreen) display.width else (display.width * 0.75).toInt()
            val height = data.resolutionHeight ?: if (data.fullscreen) display.height else (display.height * 0.75).toInt()
            val window = checkNotNull(glfwCreateWindow(width, height, data.title, if (data.fullscreen) display.pointer else null, null)) {
                "Unable to create OpenGL 4.3 window"
            }
            glfwMakeContextCurrent(window)
            nativeWindow = NativeSurface(window)
        } catch (error: Throwable) {
            glfwTerminate()
            throw error
        }
    }

    override fun setCallbacks(callbacks: EventCallbacks) {
        input?.close()
        input = NativeInput(checkNotNull(nativeWindow).window, callbacks)
    }

    override fun createSharedContext(): SurfaceContext {
        val parent = checkNotNull(nativeWindow) { "Surface not initialized" }
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        val child = try { glfwCreateWindow(1, 1, "Mechanica shared context", null, parent.window) }
        finally { glfwWindowHint(GLFW_VISIBLE, GLFW_TRUE) }
        val context = NativeSurfaceContext(false, NativeSurface(checkNotNull(child) { "Unable to create shared context" }), nativeDisplay)
        context.major = major
        context.minor = minor
        return context
    }

    override fun activate() = glfwMakeContextCurrent(checkNotNull(nativeWindow).window)

    override fun destroy() {
        input?.close(); input = null
        nativeWindow?.destroy(); nativeWindow = null
        if (ownsGlfw) glfwTerminate()
    }
}

class NativeSurface internal constructor(internal val window: CPointer<GLFWwindow>) : DrawSurface {
    private val callbacks = mutableListOf<(DrawSurface) -> Unit>()
    private var closed = false
    private var currentWidth = 0
    private var currentHeight = 0
    init { updateSize() }
    override val resolution: Resolution = object : Resolution {
        override val width get() = currentWidth.px
        override val height get() = currentHeight.px
    }
    override var vSync: Boolean = true
        set(value) { field = value; glfwSwapInterval(if (value) 1 else 0) }

    override fun addOnChangedCallback(callback: (DrawSurface) -> Unit) { callbacks += callback }
    override fun update(): Boolean {
        glfwSwapBuffers(window)
        EventCallbacks.prepare()
        glfwPollEvents()
        val oldWidth = currentWidth; val oldHeight = currentHeight
        updateSize()
        if (oldWidth != currentWidth || oldHeight != currentHeight) callbacks.forEach { it(this) }
        return glfwWindowShouldClose(window) == GLFW_FALSE
    }
    override fun shouldClose() = glfwSetWindowShouldClose(window, GLFW_TRUE)
    override fun destroy() { if (!closed) { closed = true; glfwDestroyWindow(window) } }
    private fun updateSize() = memScoped {
        val width = alloc<IntVar>(); val height = alloc<IntVar>()
        glfwGetFramebufferSize(window, width.ptr, height.ptr)
        currentWidth = width.value; currentHeight = height.value
    }
}

class NativeDisplay internal constructor(internal val pointer: CPointer<GLFWmonitor>) : Display {
    override val id: Long get() = pointer.rawValue.toLong()
    override val name: String get() = glfwGetMonitorName(pointer)?.toKString() ?: "unknown"
    override val width: Int get() = checkNotNull(glfwGetVideoMode(pointer)).pointed.width
    override val height: Int get() = checkNotNull(glfwGetVideoMode(pointer)).pointed.height
    override val physicalSize: Display.Size get() = memScoped {
        val width = alloc<IntVar>(); val height = alloc<IntVar>()
        glfwGetMonitorPhysicalSize(pointer, width.ptr, height.ptr)
        Display.Size(width.value, height.value)
    }
    override val contentScale: Display.ContentScale get() = memScoped {
        val x = alloc<FloatVar>(); val y = alloc<FloatVar>()
        glfwGetMonitorContentScale(pointer, x.ptr, y.ptr)
        Display.ContentScale(x.value, y.value)
    }
}
