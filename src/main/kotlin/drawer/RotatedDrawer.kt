package drawer

import util.units.Angle

interface RotatedDrawer : Drawer {

    operator fun invoke(angle: Angle): RotatedDrawer

    fun about(pivotX: Number, pivotY: Number): Drawer
}