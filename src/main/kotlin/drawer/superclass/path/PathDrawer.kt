package drawer.superclass.path

import util.units.LightweightVector
import util.units.Vector

interface PathDrawer {
    fun path(path: Array<Vector>)
    fun path(path: List<Vector>)

    fun line(x1: Number, y1: Number, x2: Number, y2: Number)
    fun line(p1: Vector, p2: Vector) = line(p1.x, p1.y, p2.x, p2.y)
    fun line(p1: LightweightVector, p2: LightweightVector) = line(p1.x, p1.y, p2.x, p2.y)
}