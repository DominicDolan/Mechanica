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

    internal fun render(draw: Drawer) {
        if (Game.debug.constructionDraws) {
            for (drawing in drawings) {
                drawing(draw)
            }
            drawings.clear()
        }
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
}