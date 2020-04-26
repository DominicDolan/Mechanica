package drawer.subclasses.stroke

import drawer.Drawer
import util.colors.Color
import util.colors.HexColor

interface StrokeDrawer {
    operator fun invoke(stroke: Double): Drawer

    fun strokeColor(color: Color)
    fun strokeColor(color: HexColor)
}