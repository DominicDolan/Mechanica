package drawer.subclasses.stroke

import drawer.DrawData
import drawer.Drawer
import util.colors.Color
import util.colors.HexColor

class StrokeDrawerImpl(drawer: Drawer, private val data: DrawData) : StrokeDrawer, Drawer by drawer {
    override operator fun invoke(stroke: Double): Drawer {
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