package input

object Keyboard {
    private val map = HashMap<Int, ArrayList<Key>>()

    val A = KeyboardKey(Keys.A)
    val B = KeyboardKey(Keys.B)
    val C = KeyboardKey(Keys.C)
    val D = KeyboardKey(Keys.D)
    val E = KeyboardKey(Keys.E)
    val F = KeyboardKey(Keys.F)
    val G = KeyboardKey(Keys.G)
    val H = KeyboardKey(Keys.H)
    val I = KeyboardKey(Keys.I)
    val J = KeyboardKey(Keys.J)
    val K = KeyboardKey(Keys.K)
    val L = KeyboardKey(Keys.L)
    val M = KeyboardKey(Keys.M)
    val N = KeyboardKey(Keys.N)
    val O = KeyboardKey(Keys.O)
    val P = KeyboardKey(Keys.P)
    val Q = KeyboardKey(Keys.Q)
    val R = KeyboardKey(Keys.R)
    val S = KeyboardKey(Keys.S)
    val T = KeyboardKey(Keys.T)
    val U = KeyboardKey(Keys.U)
    val V = KeyboardKey(Keys.V)
    val W = KeyboardKey(Keys.W)
    val X = KeyboardKey(Keys.X)
    val Y = KeyboardKey(Keys.Y)
    val Z = KeyboardKey(Keys.Z)

    val MB1 = KeyboardKey(Keys.M1)
    val MB2 = KeyboardKey(Keys.M2)
    val MMB = KeyboardKey(Keys.M3)
    val SPACE = KeyboardKey(Keys.SPACE)
    val SHIFT = KeyboardKey(Keys.LSHIFT)
    val CTRL = KeyboardKey(Keys.LCTRL)
    val TAB = KeyboardKey(Keys.TAB)
    val LALT = KeyboardKey(Keys.LALT)
    val ESC = KeyboardKey(Keys.ESC)
    val COMMA = KeyboardKey(Keys.COMMA)
    val PERIOD = KeyboardKey(Keys.PERIOD)

    operator fun get(keyId: Int): ArrayList<Key> {
        val keys = map[keyId]
        if (keys != null) {
            return keys
        }
        return arrayListOf(Key(map, keyId))
    }

    class KeyboardKey(vararg keys: Keys): Key(map, *keys)
}