package com.mechanica.engine.input

import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.game.Game
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWMouseButtonCallback
import org.lwjgl.glfw.GLFWScrollCallback

class MouseHandler {
    class ButtonHandler : GLFWMouseButtonCallback() {
        override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
            if (action == GLFW.GLFW_RELEASE) {
                Keyboard[button].forEach { it.isDown = false }
                Mouse[button].forEach { it.isDown = false }
                Game.controls[button].forEach { it.isDown = false }
            } else if (action == GLFW.GLFW_PRESS) {
                Keyboard[button].forEach { it.isDown = true }
                Mouse[button].forEach { it.isDown = true }
                Game.controls[button].forEach { it.isDown = true }
            }
        }

    }

    class CursorHandler : GLFWCursorPosCallback() {
        override fun invoke(window: Long, x: Double, y: Double) {
            (Mouse.pixel as DynamicVector).set(x, y)
        }

    }

    class ScrollHandler: GLFWScrollCallback() {
        override fun invoke(window: Long, xoffset: Double, yoffset: Double) {

            if (yoffset > 0.0) {
                Mouse.scrollUp.isDown = false
                Mouse.scrollUp.isDown = true
                Mouse.scrollUp.distance = yoffset
            } else if (yoffset < 0.0) {
                Mouse.scrollDown.isDown = false
                Mouse.scrollDown.isDown = true
                Mouse.scrollDown.distance = -yoffset
            }
        }

    }

}