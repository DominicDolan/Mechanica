package drawer.subclasses.rotation

import drawer.Drawer
import util.units.Angle
import util.units.Degree
import util.units.Radian

interface RotatedDrawer : Drawer {

    operator fun invoke(angle: Angle): RotatedDrawer

    fun about(pivotX: Number, pivotY: Number): Drawer
}