package input

import org.lwjgl.glfw.GLFW.*

object Controls {
    val map = HashMap<Int, Key>()

    val UP = Key(map, GLFW_KEY_W, GLFW_KEY_UP)

    operator fun get(keyId: Int): Key {
        val key = map[keyId]
        if (key != null) {
            return key
        }
        return Key(map, keyId)
    }
}