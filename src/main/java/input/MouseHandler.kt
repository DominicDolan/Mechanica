package input

import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWCursorPosCallback
import org.lwjgl.glfw.GLFWMouseButtonCallback

class MouseHandler {
    class ButtonHandler : GLFWMouseButtonCallback() {
        override fun invoke(p0: Long, p1: Int, p2: Int, p3: Int) {

        }

    }

    class CursorHandler : GLFWCursorPosCallback() {
        override fun invoke(p0: Long, p1: Double, p2: Double) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

}