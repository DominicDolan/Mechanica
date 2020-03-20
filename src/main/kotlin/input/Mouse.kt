package input

import display.Game

object Mouse {
    private val map = HashMap<Int, ArrayList<Key>>()

    val MB1 = MouseButton(Keys.M1)
    val MB2 = MouseButton(Keys.M2)
    val MMB = MouseButton(Keys.M3)
    val M4 = MouseButton(Keys.M4)
    val M5 = MouseButton(Keys.M5)
    val M6 = MouseButton(Keys.M6)
    val M7 = MouseButton(Keys.M7)
    val M8 = MouseButton(Keys.M8)

    val SCROLL_UP = ScrollWheel(Keys.SCROLL_UP)
    val SCROLL_DOWN = ScrollWheel(Keys.SCROLL_DOWN)

    operator fun get(keyId: Int): ArrayList<Key> {
        val keys = map[keyId]
        if (keys != null) {
            return keys
        }
        return arrayListOf(Key(map, keyId))
    }

    var x: Double = 0.0
        internal set(value) {
            field = value
            val ratio = Game.width/ Game.viewWidth
            viewX = value/ratio - Game.viewWidth/2.0
            worldX = viewX + Game.viewX
        }

    var y: Double = 0.0
        internal set(value) {
            field = value
            val ratio = Game.height/ Game.viewHeight
            viewY = Game.viewHeight/2.0 - value/ratio
            worldY = viewY + Game.viewY

        }

    var viewX: Double = 0.0; internal set
    var viewY: Double = 0.0; internal set

    var worldX: Double = 0.0; internal set
    var worldY: Double = 0.0; internal set


    class MouseButton(vararg keys: Keys): Key(map, *keys)
    class ScrollWheel(vararg keys: Keys): Key(map, *keys) {
        var distance = 0.0
            internal set
    }
}