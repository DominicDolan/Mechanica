package input

import org.lwjgl.glfw.GLFW.*

object Keyboard {
    private val map = HashMap<Int, ArrayList<Key>>()

    val Q = KeyboardKey(Keys.Q)
    val W = KeyboardKey(Keys.W)
    val E = KeyboardKey(Keys.E)
    val R = KeyboardKey(Keys.R)
    val T = KeyboardKey(Keys.T)
    val A = KeyboardKey(Keys.A)
    val S = KeyboardKey(Keys.S)
    val D = KeyboardKey(Keys.D)
    val F = KeyboardKey(Keys.F)
    val Z = KeyboardKey(Keys.Z)
    val X = KeyboardKey(Keys.X)
    val C = KeyboardKey(Keys.C)
    val V = KeyboardKey(Keys.V)
    val M1 = KeyboardKey(Keys.M1)
    val M2 = KeyboardKey(Keys.M2)
    val SPACE = KeyboardKey(Keys.SPACE)
    val SHIFT = KeyboardKey(Keys.LSHIFT)
    val CTRL = KeyboardKey(Keys.LCTRL)
    val TAB = KeyboardKey(Keys.TAB)
    val LALT = KeyboardKey(Keys.LALT)
    val ESC = KeyboardKey(Keys.ESC)

    operator fun get(keyId: Int): ArrayList<Key> {
        val keys = map[keyId]
        if (keys != null) {
            return keys
        }
        return arrayListOf(Key(map, keyId))
    }

    class KeyboardKey(vararg keys: Keys): Key(map, *keys)
}