package com.mechanica.engine.display

import com.mechanica.engine.context.GLFWContext
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWGammaRamp
import org.lwjgl.glfw.GLFWMonitorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.system.MemoryStack

class GLFWMonitor private constructor(override val id: Long) : Monitor {

    override val name by lazy { glfwGetMonitorName(id) ?: "N/A" }

    override val width: Int
        get() = currentVideoMode.width()
    override val height: Int
        get() = currentVideoMode.height()

    var monitorUserPointer: Long
        get() = glfwGetMonitorUserPointer(id)
        set(value) {
            glfwSetMonitorUserPointer(id, value)
        }

    val currentVideoMode: GLFWVidMode
        get() = glfwGetVideoMode(id) ?: throw IllegalStateException("Error retrieving the current Vid Mode")

    val supportedVideoModes: Array<GLFWVidMode>
        get() {
            val vidmodes = glfwGetVideoModes(id)
            return if (vidmodes != null) {
                Array(vidmodes.limit()) { vidmodes[it] }
            } else {
                emptyArray()
            }
        }

    override val size: Monitor.Size
        get() {
            var width = -1
            var height = -1
            MemoryStack.stackPush().use { stack ->
                val widthBuffer = stack.ints(0)
                val heightBuffer = stack.ints(0)
                glfwGetMonitorPhysicalSize(id, widthBuffer, heightBuffer)
                width = widthBuffer[0]
                height = heightBuffer[0]
            }
            return Monitor.Size(width, height)
        }

    override val contentScale: Monitor.ContentScale
        get() {
            var xScale = 1f
            var yScale = 1f
            MemoryStack.stackPush().use { stack ->
                val xBuffer = stack.floats(0f)
                val yBuffer = stack.floats(0f)
                glfwGetMonitorContentScale(id, xBuffer, yBuffer)
                xScale = xBuffer[0]
                yScale = yBuffer[0]
            }
            return Monitor.ContentScale(xScale, yScale)
        }

    var gammaRamp: GLFWGammaRamp
        get() = glfwGetGammaRamp(id) ?: throw IllegalStateException("GLFW error when getting the gamma ramp")
        set(value) {
            glfwSetGammaRamp(id, value)
        }

    var connectionCallback: GLFWMonitorCallback? = null
        set(value) {
            if (value != null) {
                glfwSetMonitorCallback(value)
            }
            field = value
        }

    companion object {
        var allMonitors = createMonitorsArray()
            private set
            get() {
                field = checkMonitors(field)
                return field
            }

        fun getPrimaryMonitor(): GLFWMonitor {
            GLFWContext.initialize()
            return GLFWMonitor(glfwGetPrimaryMonitor())
        }

        private fun createMonitorsArray(): Array<GLFWMonitor> {
            GLFWContext.initialize()
            val pointers = glfwGetMonitors()
            return if (pointers != null) {
                Array(pointers.limit()) {GLFWMonitor(pointers[it])}
            } else {
                emptyArray()
            }
        }

        private fun checkMonitors(monitors: Array<GLFWMonitor>): Array<GLFWMonitor> {
            val pointers = glfwGetMonitors()
            if (pointers != null) {
                val pointerSize = pointers.limit()
                if (pointerSize != monitors.size ) {
                    return createMonitorsArray()
                }

                for (p in 0 until pointers.limit()) {
                    monitors.firstOrNull { it.id == p.toLong() } ?: return createMonitorsArray()
                }
            }
            return monitors
        }
    }
}