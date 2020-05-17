@file:Suppress("unused") // There will be many functions here that go unused most of the time
package com.mechanica.engine.debug

import com.mechanica.engine.unit.angle.Angle
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.plus
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game

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
        drawings.add { it.stroke(0.4).line(position, vec(position) + vec(vector)) }
    }

    fun drawVector(position: Vector, r: Number, theta: Angle) {
        drawVector(position, vec(r, theta))
    }
}