package display

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback

object GLFWContext : Version {
    override val majorVersion: Int
    override val minorVersion: Int
    override val version: Double

    private var _samples = 4
    val samples: Int
        get() = _samples

    init {
        val majorArray = IntArray(1)
        val minorArray = IntArray(1)
        val revisionArray = IntArray(1)

        glfwGetVersion(majorArray, minorArray, revisionArray)

        majorVersion = majorArray[0]
        minorVersion = minorArray[0]

        version = "$majorVersion.$minorVersion".toDouble()
    }

    fun initialize() {
        if (_samples != 0) {
            glfwWindowHint(GLFW_SAMPLES, samples)
        }

        check(glfwInit()) { "Unable to initialize GLFW" }

        GLFWErrorCallback.createPrint(System.err).set()
    }

    fun terminate() {
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    fun multisampling(samples: Int) {
        _samples = samples
    }
}