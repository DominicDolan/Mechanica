package drawer.subclasses.stroke

import drawer.DrawData
import drawer.Drawer2
import util.colors.Color
import util.colors.HexColor

class StrokeDrawerImpl(drawer: Drawer2, private val data: DrawData) : StrokeDrawer2, Drawer2 by drawer {
    override operator fun invoke(stroke: Double): Drawer2 {
        data.strokeWidth = stroke
        return this
    }

    override fun strokeColor(color: Color) {
        data.strokeColor.set(color)
    }

    override fun strokeColor(color: HexColor) {
        data.strokeColor.set(color)
    }
}