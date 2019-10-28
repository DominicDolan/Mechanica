package debug

import graphics.drawer.Drawer
import util.units.Vector

object DebugDrawer {
    private val drawings = ArrayList<(Drawer) -> Unit>()

    internal fun render(draw: Drawer) {
        for (drawing in drawings) {
            drawing(draw)
        }
        drawings.clear()
    }

    fun drawCircle(position: Vector, radius: Double) {
        drawings.add { it.stroke(0.2).circle(position, radius) }
    }


}