package drawer.subclasses.layout

import drawer.Drawer2
import util.units.LightweightVector
import util.units.Vector

interface LayoutDrawer {
    fun origin(): Drawer2
    fun origin(x: Number, y: Number): Drawer2
    fun origin(point: Vector) = origin(point.x, point.y)
    fun origin(point: LightweightVector) = origin(point.x, point.y)
}