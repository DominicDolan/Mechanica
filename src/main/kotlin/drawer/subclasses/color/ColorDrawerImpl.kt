package drawer.subclasses.color

import drawer.DrawData
import drawer.Drawer
import util.colors.Color
import util.colors.HexColor

internal class ColorDrawerImpl(drawer: Drawer, private val data: DrawData): ColorDrawer, Color by data.fillColor, Drawer by drawer {

    override fun get() = data.fillColor

    override fun invoke(color: Color): Drawer {
        data.fillColor.set(color)
        return this
    }

    override fun invoke(color: HexColor): Drawer {
        data.fillColor.set(color)
        return this
    }

    override fun fillColor(color: Color): ColorDrawer {
        invoke(color)
        return this
    }

    override fun fillColor(color: HexColor): ColorDrawer {
        invoke(color)
        return this
    }

    override fun strokeColor(color: Color, strokeWidth: Double): ColorDrawer {
        if (strokeWidth >= 0.0) {
            data.strokeWidth = strokeWidth
        }
        data.strokeColor.set(color)
        return this
    }

    override fun strokeColor(color: HexColor, strokeWidth: Double): ColorDrawer {
        if (strokeWidth >= 0.0) {
            data.strokeWidth = strokeWidth
        }
        data.strokeColor.set(color)
        return this
    }
}