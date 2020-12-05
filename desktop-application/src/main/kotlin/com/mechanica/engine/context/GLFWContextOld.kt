package com.mechanica.engine.context

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback

object GLFWContextOld : Version {
    override val majorVersion: Int
    override val minorVersion: Int
    override val version: String

    var initialized = false
        private set

    private var _samples = 4
    val samples: Int
        get() = _samples

    init {
        initialize()
        val majorArray = IntArray(1)
        val minorArray = IntArray(1)
        val revisionArray = IntArray(1)

        glfwGetVersion(majorArray, minorArray, revisionArray)

        majorVersion = majorArray[0]
        minorVersion = minorArray[0]

        version = "$majorVersion.$minorVersion"
    }

    fun initialize() {
        if (!initialized) {
            check(glfwInit()) { "Unable to initialize GLFW" }

            if (_samples != 0) {
                glfwWindowHint(GLFW_SAMPLES, samples)
            }

            GLFWErrorCallback.createPrint(System.err).set()
            initialized = true
        }
    }

    fun terminate() {
        glfwSetErrorCallback(null)?.free()
        glfwTerminate()
        initialized = false
    }

    fun multisampling(samples: Int) {
        _samples = samples
    }
}