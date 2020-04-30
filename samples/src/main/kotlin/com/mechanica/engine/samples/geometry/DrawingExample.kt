package com.mechanica.engine.samples.geometry

import com.mechanica.engine.color.rgba
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.shader.PathRenderer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.Keyboard
import com.mechanica.engine.input.Mouse
import com.mechanica.engine.state.State
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.distanceTo
import com.mechanica.engine.unit.vector.vec
import org.joml.Matrix4f

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingState { DrawingExample() }
    }

    Game.run()
}

class DrawingExample : State() {
    private val renderer = PathRenderer()

    private val transformation = Matrix4f()

    private val paths = ArrayList<ArrayList<Vector>>()
    private val strokes = ArrayList<Float>()
    private val minLineLength = 0.05

    init {
        val initialStroke = 0.2f
        renderer.stroke = initialStroke
        strokes.add(initialStroke)

        paths.add(ArrayList())
        transformation.identity()
    }

    override fun update(delta: Double) {
        if (!Keyboard.SHIFT() && Keyboard.MB1.hasBeenReleased) {
            paths.add(ArrayList())
            strokes.add(strokes.last())
        }

        if (Keyboard.MB1()) {
            val path = paths.last()
            val cursor = vec(Mouse.world)
            val addPoint = if (path.size == 0) true else path.last().distanceTo(cursor) > minLineLength

            if (addPoint) {
                path.add(cursor)
            }
        }

        if (Keyboard.W.hasBeenPressed) {
            val newStroke = strokes.last()*1.1f
            strokes[strokes.size-1] = newStroke
        }
        if (Keyboard.S.hasBeenPressed) {
            val newStroke = strokes.last()/1.1f
            strokes[strokes.size-1] = newStroke
        }
        if (Keyboard.CTRL() && Keyboard.Z.hasBeenPressed) {
            paths.remove(paths.last())
            strokes.remove(strokes.last())
        }
    }

    override fun render(draw: Drawer) {
        renderer.color = rgba(1.0, 0.0, 0.0, 0.5)
        for (i in paths.indices) {
            renderer.fillFloats(paths[i])
            renderer.stroke = strokes[i]
            renderer.render(transformation)
        }
        if (!Keyboard.MB1()) {
            draw.color(renderer.color).circle(Mouse.world, strokes.last() / 2f)
        }
    }

}