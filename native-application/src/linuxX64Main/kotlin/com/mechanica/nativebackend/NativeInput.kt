@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.mechanica.nativebackend

import com.mechanica.engine.context.callbacks.EventCallbacks
import com.mechanica.engine.context.loader.InputFactory
import com.mechanica.samples.nativegl.*
import kotlinx.cinterop.*
import cnames.structs.GLFWwindow

class NativeInputFactory : InputFactory {
    override fun keyIds() = NativeKeyIDs()
}

/** Owns the window user pointer until closed; callbacks must be installed on the GLFW thread. */
class NativeInput(window: CPointer<GLFWwindow>, callbacks: EventCallbacks) {
    private val window = window
    private val reference: StableRef<EventCallbacks>
    private var closed = false

    init {
        check(glfwGetWindowUserPointer(window) == null) { "Window user pointer is already in use" }
        reference = StableRef.create(callbacks)
        glfwSetWindowUserPointer(window, reference.asCPointer())
        glfwSetKeyCallback(window, staticCFunction { handle, key, _, action, _ ->
            val callback = callbacksFor(handle)?.keyboardHandler
            when (action) {
                GLFW_PRESS -> callback?.keyPressed(key)
                GLFW_RELEASE -> callback?.keyReleased(key)
            }
        })
        glfwSetCharCallback(window, staticCFunction { handle, codepoint ->
            callbacksFor(handle)?.keyboardHandler?.textInput(codepoint.toInt())
        })
        glfwSetMouseButtonCallback(window, staticCFunction { handle, button, action, _ ->
            val callback = callbacksFor(handle)?.mouseHandler
            when (action) {
                GLFW_PRESS -> callback?.buttonPressed(button)
                GLFW_RELEASE -> callback?.buttonReleased(button)
            }
        })
        glfwSetCursorPosCallback(window, staticCFunction { handle, x, y ->
            callbacksFor(handle)?.mouseHandler?.cursorMoved(x, y)
        })
        glfwSetScrollCallback(window, staticCFunction { handle, x, y ->
            callbacksFor(handle)?.mouseHandler?.scroll(x, y)
        })
    }

    fun close() {
        if (closed) return
        closed = true
        glfwSetKeyCallback(window, null)
        glfwSetCharCallback(window, null)
        glfwSetMouseButtonCallback(window, null)
        glfwSetCursorPosCallback(window, null)
        glfwSetScrollCallback(window, null)
        glfwSetWindowUserPointer(window, null)
        reference.dispose()
    }
}

private fun callbacksFor(window: CPointer<GLFWwindow>?): EventCallbacks? =
    window?.let { glfwGetWindowUserPointer(it)?.asStableRef<EventCallbacks>()?.get() }
