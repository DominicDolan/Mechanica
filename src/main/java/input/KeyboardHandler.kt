package input

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWKeyCallback

/**
 * Created by domin on 29/10/2017.
 */
class KeyboardHandler : GLFWKeyCallback() {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (action == GLFW_RELEASE) {
            setKey(key, false)
        } else if (action == GLFW_PRESS) {
            setKey(key, false)
        }

        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
            glfwSetWindowShouldClose(window, true) // We will detect this in the rendering update
    }

    private fun setKey(key: Int, value: Boolean) {
        when (key) {
            GLFW_KEY_Q -> Keyboard.Q = value
            GLFW_KEY_W -> Keyboard.W = value
            GLFW_KEY_E -> Keyboard.E = value
            GLFW_KEY_R -> Keyboard.R = value
            GLFW_KEY_T -> Keyboard.T = value
            GLFW_KEY_A -> Keyboard.A = value
            GLFW_KEY_S -> Keyboard.S = value
            GLFW_KEY_D -> Keyboard.D = value
            GLFW_KEY_F -> Keyboard.F = value
            GLFW_KEY_Z -> Keyboard.Z = value
            GLFW_KEY_X -> Keyboard.X = value
            GLFW_KEY_C -> Keyboard.C = value
            GLFW_KEY_V -> Keyboard.V = value
            GLFW_KEY_LEFT_SHIFT -> Keyboard.SHIFT = value
            GLFW_KEY_LEFT_CONTROL -> Keyboard.CTRL = value
            GLFW_KEY_TAB -> Keyboard.TAB = value
            GLFW_KEY_LEFT_ALT -> Keyboard.LALT = value
            GLFW_KEY_ESCAPE -> Keyboard.ESC = value
        }
    }
}