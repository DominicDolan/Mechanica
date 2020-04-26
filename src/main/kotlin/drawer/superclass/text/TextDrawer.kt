package drawer.superclass.text

import util.units.LightweightVector
import util.units.Vector

interface TextDrawer {
    fun text(text: String)

    fun text(text: String, size: Number, x: Number, y: Number)
    fun text(text: String, size: Number, position: Vector) = text(text, size, position.x, position.y)
    fun text(text: String, size: Number, position: LightweightVector) = text(text, size, position.x, position.y)
}