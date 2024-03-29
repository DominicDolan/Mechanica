package com.mechanica.engine.samples.geometry

import com.cave.library.color.rgba
import com.cave.library.vector.vec2.*
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.scenes.scenes.Scene

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { BezierCurve() }
    }

    Game.loop()
}

class BezierCurve : Scene() {

    private val maxVertices = 1000
    private val minLineLength = 0.1

    private val vertices = ArrayList<MutableVector2>(maxVertices)
    private var index = 0

    var p1 = MutableVector2.create(-6.0, -3.0)
    var p2 = MutableVector2.create(6.0, -3.0)
    var bezierPoint = MutableVector2.create(0.0, 3.0)

    private val constructionPoints = arrayOf(p1, bezierPoint, p2)

    init {
        bezier(vec(p1), vec(bezierPoint), vec(p2))
        reset()
    }

    override fun update(delta: Double) {
        if (Mouse.MB1() && Keyboard.shift()) {
            bezierPoint.set(Mouse.world)
            reset()
        } else if (Mouse.MB1()) {
            p1.set(Mouse.world)
            reset()
        }else if (Mouse.MB2()) {
            p2.set(Mouse.world)
            reset()
        }
    }

    override fun render(draw: Drawer) {
        draw.color.strokeColor(rgba(0.5, 0.8, 0.5, 1.0), 0.05).path(constructionPoints)
        draw.color.strokeColor(rgba(0.5, 0.5, 0.5, 1.0), 0.05).path(vertices, count = index)
    }

    private fun bezier(p1: InlineVector, p2: InlineVector, p3: InlineVector) {
        val p12 = p2 - p1
        val p23 = p3 - p2
        val p13 = p3 - p1
        val iterations = p13.r/minLineLength

        val increment = 1.0/iterations
        var t = increment

        while (t < 1.0) {
            val p12t = p1 + p12*t
            val p23t = p2 + p23*t

            val relative = p23t - p12t
            val point = p12t + relative*t
            addPoint(point)
            t += increment
        }

    }

    private fun reset() {
        index = 0
//        for(i in floats.indices) {
//            floats[i] = 0f
//        }
        bezier(vec(p1), vec(bezierPoint), vec(p2))
//        updateConstructionLines()
    }

    private fun addPoint(vector: Vector2) {
        if (index < vertices.size) {
            vertices[index].set(vector)
            index++
        } else {
            vertices.add(MutableVector2.create(vector))
        }
    }

//    private fun updateConstructionLines() {
//        constructionFloats[0] = p1.x.toFloat()
//        constructionFloats[1] = p1.y.toFloat()
//        constructionFloats[3] = bezierPoint.x.toFloat()
//        constructionFloats[4] = bezierPoint.y.toFloat()
//        constructionFloats[6] = p2.x.toFloat()
//        constructionFloats[7] = p2.y.toFloat()
//        constructionLines.update(constructionFloats)
//    }

}