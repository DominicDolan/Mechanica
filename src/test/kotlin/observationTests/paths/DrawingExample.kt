package demo.paths

import drawer.Drawer
import drawer.shader.PathRenderer
import game.Game
import gl.models.Model
import input.Keyboard
import input.Mouse
import org.joml.Matrix4f
import state.State
import util.colors.rgba
import util.extensions.distanceTo
import util.extensions.vec
import util.units.Vector

class DrawingExample : State() {
    val renderer = PathRenderer()

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

        if (Keyboard.MB1.isDown) {
            val path = paths.last()
            val cursor = vec(Mouse.view.x, Mouse.view.y)
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
        if (Keyboard.CTRL.isDown && Keyboard.Z.hasBeenPressed) {
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
    }

}


