package drawer.subclasses.color

import drawer.DrawData
import drawer.Drawer2
import drawer.shader.DrawerRenderer
import util.colors.Color
import util.colors.HexColor
import util.colors.rgba2Hex
import util.colors.toColor

internal class ColorDrawerImpl(drawer: Drawer2, private val data: DrawData): ColorDrawer2, Color by data.fillColor, Drawer2 by drawer {

    override fun get() = data.fillColor

    override fun invoke(color: Color): Drawer2 {
        data.fillColor.set(color)
        return this
    }

    override fun invoke(color: HexColor): Drawer2 {
        data.fillColor.set(color)
        return this
    }

    override fun fillColor(color: Color): ColorDrawer2 {
        invoke(color)
        return this
    }

    override fun fillColor(color: HexColor): ColorDrawer2 {
        invoke(color)
        return this
    }

    override fun strokeColor(color: Color, strokeWidth: Double): ColorDrawer2 {
        if (strokeWidth >= 0.0) {
            data.strokeWidth = strokeWidth
        }
        data.strokeColor.set(color)
        return this
    }

    override fun strokeColor(color: HexColor, strokeWidth: Double): ColorDrawer2 {
        if (strokeWidth >= 0.0) {
            data.strokeWidth = strokeWidth
        }
        data.strokeColor.set(color)
        return this
    }
}