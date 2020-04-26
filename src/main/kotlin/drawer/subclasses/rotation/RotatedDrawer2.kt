package drawer.subclasses.rotation

import drawer.Drawer2
import util.units.Degree
import util.units.LightweightVector
import util.units.Radian
import util.units.Vector

interface RotatedDrawer2 : Drawer2 {

    operator fun invoke(angle: Degree): RotatedDrawer2
    operator fun invoke(angle: Radian): RotatedDrawer2

    fun about(pivotX: Number, pivotY: Number): Drawer2
    fun about(point: LightweightVector): Drawer2
    fun about(point: Vector): Drawer2
}