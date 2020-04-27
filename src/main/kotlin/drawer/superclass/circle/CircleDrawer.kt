package drawer.superclass.circle

import util.units.LightweightVector
import util.units.Vector

interface CircleDrawer {
    fun circle()

    fun circle(x: Number, y: Number)
    fun circle(x: Number, y: Number, radius: Number)

    fun circle(position: Vector) = circle(position.x, position.y)
    fun circle(position: LightweightVector) = circle(position.x, position.y)

    fun circle(position: Vector, radius: Number) = circle(position.x, position.y, radius)
    fun circle(position: LightweightVector, radius: Number) = circle(position.x, position.y, radius)

    fun ellipse(x: Number, y: Number, width: Number, height: Number)
    fun ellipse(xy: LightweightVector, wh: LightweightVector) = ellipse(xy.x, xy.y, wh.x, wh.y)
}