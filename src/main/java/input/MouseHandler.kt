package input

import display.Game
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWMouseButtonCallback

class MouseHandler {
    class ButtonHandler : GLFWMouseButtonCallback() {
        override fun invoke(window: Long, button: Int, action: Int, mods: Int) {
            if (action == GLFW.GLFW_RELEASE) {
                Keyboard[button].forEach { it.isDown = false }
                Game.controls[button].forEach { it.isDown = false }
            } else if (action == GLFW.GLFW_PRESS) {
                Keyboard[button].forEach { it.isDown = true }
                Game.controls[button].forEach { it.isDown = true }
            }
        }

    }

    class CursorHandler : GLFWCursorPosCallback() {
        override fun invoke(window: Long, x: Double, y: Double) {
            Cursor.x = x
            Cursor.y = y
        }

    }

}