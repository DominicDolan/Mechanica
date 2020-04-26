package drawer.subclasses.rotation

import drawer.Drawer
import util.units.Degree
import util.units.LightweightVector
import util.units.Radian
import util.units.Vector

interface RotatedDrawer : Drawer {

    operator fun invoke(angle: Degree): RotatedDrawer
    operator fun invoke(angle: Radian): RotatedDrawer

    fun about(pivotX: Number, pivotY: Number): Drawer
    fun about(point: LightweightVector): Drawer
    fun about(point: Vector): Drawer
}