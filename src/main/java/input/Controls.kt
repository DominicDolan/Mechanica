package input

import org.lwjgl.glfw.GLFW.*

object Controls {
    private val map = HashMap<Int, ArrayList<Key>>()

    var UP = ControlsKey(Keys.W, Keys.UP)
    var JUMP = ControlsKey(Keys.SPACE, Keys.W)

    operator fun get(keyId: Int): ArrayList<Key> {
        val keys = map[keyId]
        if (keys != null) {
            return keys
        }
        return arrayListOf(Key(map, keyId))
    }

    class ControlsKey(vararg keys: Keys) : Key(map, *keys)
}