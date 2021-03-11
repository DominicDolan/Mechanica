package com.mechanica.engine.samples.geometry

import com.cave.library.color.rgba
import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.distanceTo
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.shader.PathRenderer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.scenes.scenes.WorldScene

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setFullscreen(false)
        setStartingScene { DrawingExample() }
    }

    Game.loop()
}

class DrawingExample : WorldScene() {
    private val renderer = PathRenderer()

    private val transformation = Matrix4.identity()

    private val paths = ArrayList<ArrayList<Vector2>>()
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
        if (!Keyboard.shift() && Mouse.MB1.hasBeenReleased) {
            paths.add(ArrayList())
            strokes.add(strokes.last())
        }

        if (Mouse.MB1()) {
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
        if (Keyboard.ctrl() && Keyboard.Z.hasBeenPressed) {
            paths.remove(paths.last())
            strokes.remove(strokes.last())
        }
    }

    override fun render(draw: Drawer) {
        val color = rgba(1.0, 0.0, 0.0, 0.5)
        renderer.color = color
        for (i in paths.indices) {
            renderer.fillFloats(paths[i])
            renderer.stroke = strokes[i]
            renderer.render(transformation)
        }
        if (!Mouse.MB1()) {
            draw.color(color).circle(Mouse.world, strokes.last() / 2f)
        }
    }

}