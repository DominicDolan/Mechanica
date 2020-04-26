package drawer.subclasses.stroke

import drawer.Drawer
import drawer.Drawer2
import util.colors.Color
import util.colors.HexColor

interface StrokeDrawer2 {
    operator fun invoke(stroke: Double): Drawer2

    fun strokeColor(color: Color)
    fun strokeColor(color: HexColor)
}