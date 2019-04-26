package input

import org.lwjgl.glfw.GLFW

object Keyboard {
    internal val map = HashMap<Int, Key>()

    val Q = Key(map, GLFW.GLFW_KEY_Q)
    val W = Key(map, GLFW.GLFW_KEY_W)
    val E = Key(map, GLFW.GLFW_KEY_E)
    val R = Key(map, GLFW.GLFW_KEY_R)
    val T = Key(map, GLFW.GLFW_KEY_T)
    val A = Key(map, GLFW.GLFW_KEY_A)
    val S = Key(map, GLFW.GLFW_KEY_S)
    val D = Key(map, GLFW.GLFW_KEY_D)
    val F = Key(map, GLFW.GLFW_KEY_F)
    val Z = Key(map, GLFW.GLFW_KEY_Z)
    val X = Key(map, GLFW.GLFW_KEY_X)
    val C = Key(map, GLFW.GLFW_KEY_C)
    val V = Key(map, GLFW.GLFW_KEY_V)
    val SPACE = Key(map, GLFW.GLFW_KEY_SPACE)
    val SHIFT = Key(map, GLFW.GLFW_KEY_LEFT_SHIFT)
    val CTRL = Key(map, GLFW.GLFW_KEY_LEFT_CONTROL)
    val TAB = Key(map, GLFW.GLFW_KEY_TAB)
    val LALT = Key(map, GLFW.GLFW_KEY_LEFT_ALT)
    val ESC = Key(map, GLFW.GLFW_KEY_ESCAPE)

    operator fun get(keyId: Int): Key {
        val key = map[keyId]
        if (key != null) {
            return key
        }
        return Key(map, keyId)
    }

}