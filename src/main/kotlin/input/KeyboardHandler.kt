package input

import display.Game
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCharCallback
import org.lwjgl.glfw.GLFWKeyCallback

/**
 * Created by domin on 29/10/2017.
 */
class KeyboardHandler {
    class KeyHandler : GLFWKeyCallback() {
        override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
            if (action == GLFW_PRESS) {
                Keyboard.addPressed(key)
                Game.controls[key].forEach { it.isDown = true }
            } else if (action == GLFW_RELEASE) {
                Keyboard.removePressed(key)
                Game.controls[key].forEach { it.isDown = false }
            }
        }

    }

    class TextHandler : GLFWCharCallback() {
        override fun invoke(window: Long, codepoint: Int) {
            Keyboard.textInput.setInputTextChar(codepoint)
        }

    }
}