package input

import game.Game
import util.units.DynamicVector
import util.units.Vector

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

    private val worldRatio: Vector = object : Vector {
        override val x: Double
            get() = Game.window.width/ Game.view.width
        override val y: Double
            get() = Game.window.height/ Game.view.height
    }

    private val uiRatio: Vector = object : Vector {
        override val x: Double
            get() = Game.window.width/ Game.ui.width
        override val y: Double
            get() = Game.window.height/ Game.ui.height

    }

    val pixel: Vector = object : DynamicVector {
        override var x: Double = 0.0
            set(value) {
                field = value
                setX(value)
            }
        override var y: Double = 0.0
            set(value) {
                field = value
                setY(value)
            }
    }

    val world: Vector = DynamicVector.create()
    val view: Vector = DynamicVector.create()

    fun refreshCursor() {
        setX(pixel.x)
        setY(pixel.y)
    }

    private fun setX(value: Double) {
        val viewX = value/uiRatio.x - Game.ui.width/2.0
        val worldX = value/worldRatio.x - Game.view.width/2.0 + Game.view.x

        setView(x = viewX)
        setWorld(x = worldX)
    }

    private fun setY(value: Double) {
        val viewY = Game.ui.height/2.0 - value/uiRatio.y
        val worldY = Game.view.height/2.0 - value/worldRatio.y + Game.view.y

        setView(y = viewY)
        setWorld(y = worldY)
    }

    private fun setWorld(x: Double = world.x, y: Double = world.y) {
        (world as DynamicVector).set(x, y)
    }

    private fun setView(x: Double = view.x, y: Double = view.y) {
        (view as DynamicVector).set(x, y)
    }

    private fun setPixel(x: Double = pixel.x, y: Double = pixel.y) {
        (pixel as DynamicVector).set(x, y)
    }

    class MouseButton(vararg keys: Keys): Key(map, *keys)
    class ScrollWheel(vararg keys: Keys): Key(map, *keys) {
        var distance = 0.0
            internal set
    }
}