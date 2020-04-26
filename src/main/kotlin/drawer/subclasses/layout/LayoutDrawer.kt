package drawer.subclasses.layout

import drawer.Drawer
import util.units.LightweightVector
import util.units.Vector

interface LayoutDrawer {
    fun origin(): Drawer
    fun origin(x: Number, y: Number): Drawer
    fun origin(point: Vector) = origin(point.x, point.y)
    fun origin(point: LightweightVector) = origin(point.x, point.y)
}