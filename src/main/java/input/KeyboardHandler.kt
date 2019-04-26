package input

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWKeyCallback

/**
 * Created by domin on 29/10/2017.
 */
class KeyboardHandler : GLFWKeyCallback() {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (action == GLFW_RELEASE) {
            Keyboard[key].isDown = false
            Controls[key].isDown = false
        } else if (action == GLFW_PRESS) {
            Keyboard[key].isDown = true
            Controls[key].isDown = true
        }
    }
}