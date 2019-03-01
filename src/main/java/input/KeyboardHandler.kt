package input

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWKeyCallback

/**
 * Created by domin on 29/10/2017.
 */
class KeyboardHandler : GLFWKeyCallback() {
    override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (key == GLFW.GLFW_KEY_W){
//            inverseRes[0] *= 1.005f
//            inverseRes[1] = inverseRes[0]*0.5625f
        }

        if (key == GLFW.GLFW_KEY_S){
//            inverseRes[0] /= 1.005f
//            inverseRes[1] = inverseRes[0]*0.5625f
        }

        if (action == GLFW.GLFW_RELEASE){

        }

        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
            GLFW.glfwSetWindowShouldClose(window, true) // We will detect this in the rendering update
    }
}