@file:Suppress("unused") // There will be many functions here that go unused most of the time
package debug

import drawer.Drawer
import game.Game
import util.extensions.plus
import util.extensions.vec
import util.units.Angle
import util.units.Vector

object DebugDrawer {
    private val drawings = ArrayList<(Drawer) -> Unit>()
    private val sb = StringBuilder()

    internal fun render(draw: Drawer) {
        for (drawing in drawings) {
            drawing(draw)
        }
        draw.ui.green.alpha(0.5).text(sb.toString(), 0.2, -Game.view.width/2.0, Game.view.height/2.0 - 0.2)
        drawings.clear()
        sb.clear()
    }

    fun drawCircle(position: Vector, radius: Double) {
        drawings.add { it.stroke(0.2).circle(position, radius) }
    }

    fun drawLine(x1: Number, y1: Number, x2: Number, y2: Number) {
        drawings.add { it.stroke(0.4).line(x1,y1,x2,y2) }
    }

    fun drawVector(position: Vector, vector: Vector) {
        drawings.add { it.stroke(0.4).line(position, position + vector) }
    }

    fun drawVector(position: Vector, r: Number, theta: Angle) {
        drawVector(position, vec(r, theta))
    }

    fun drawText(text: String) {
        sb.appendln(text)
    }

}