package drawer.subclasses.layout

import drawer.DrawData
import drawer.Drawer2
import util.units.LightweightVector
import util.units.Vector

class LayoutDrawerImpl(drawer: Drawer2, private val data:  DrawData) : LayoutDrawer, Drawer2 by drawer {

    override fun origin(): Drawer2 {
        data.modelOrigin.set(0.0, 0.0)
        return this
    }

    override fun origin(x: Number, y: Number): Drawer2 {
        data.modelOrigin.set(x.toDouble(), y.toDouble())
        return this
    }
}