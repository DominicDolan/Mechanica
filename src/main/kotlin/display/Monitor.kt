package display

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWGammaRamp
import org.lwjgl.glfw.GLFWMonitorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.system.MemoryStack

class Monitor private constructor(val id: Long) {

    val name by lazy { glfwGetMonitorName(id) ?: "N/A" }

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

    val size: Size
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
            return Size(width, height)
        }

    val contentScale: ContentScale
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
            return ContentScale(xScale, yScale)
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

    data class Size(val width_mm: Int, val height_mm: Int)

    data class ContentScale(val xScale: Float, val yScale: Float)

    companion object {
        var allMonitors = createMonitorsArray()
            private set
            get() {
                field = checkMonitors(field)
                return field
            }

        fun getPrimaryMonitor(): Monitor {
            GLFWContext.initialize()
            return Monitor(glfwGetPrimaryMonitor())
        }

        private fun createMonitorsArray(): Array<Monitor> {
            GLFWContext.initialize()
            val pointers = glfwGetMonitors()
            return if (pointers != null) {
                Array(pointers.limit()) {Monitor(pointers[it])}
            } else {
                emptyArray()
            }
        }

        private fun checkMonitors(monitors: Array<Monitor>): Array<Monitor> {
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