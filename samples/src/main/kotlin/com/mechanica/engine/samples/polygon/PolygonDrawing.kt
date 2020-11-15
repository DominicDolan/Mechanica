package com.mechanica.engine.samples.polygon

import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.geometry.isInTriangle
import com.mechanica.engine.geometry.lines.LineSegment
import com.mechanica.engine.geometry.triangulation.lists.TriangulatorList
import com.mechanica.engine.geometry.triangulation.triangulators.DiagonalTriangulator
import com.mechanica.engine.geometry.triangulation.triangulators.GrahamScanTriangulator
import com.mechanica.engine.input.Inputs
import com.mechanica.engine.models.PolygonModel
import com.mechanica.engine.scenes.scenes.WorldScene
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.minus
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.util.extensions.fori
import com.mechanica.engine.util.extensions.indexLooped
import kotlin.math.abs

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setFullscreen(false)
        setStartingScene { PolygonDrawing() }
    }

    Game.loop()
}

class PolygonDrawing : WorldScene(), Inputs by Inputs.create() {
    private val selectionRadius = 0.07

    private val vertices = ArrayList<Vector>()

    private var model: PolygonModel? = null

    private var diagonal = LineSegment.invoke(vec(0.0, 0.0), vec(0.0, 0.0))

    private val triangleVertices = ArrayList<Vector>()
    private var farthestVertex: Vector? = null

    private var diagonalTriangulator: DiagonalTriangulator? = null

    private val list = TriangulatorList<Vector>()

    override fun update(delta: Double) {
        super.update(delta)

        handleInput()

        if (keyboard.space.hasBeenPressed) {
            val array = vertices.toTypedArray()
            model = PolygonModel(array, triangulator = GrahamScanTriangulator(vertices.toTypedArray()))
        }
    }

    override fun render(draw: Drawer) {
        model?.let {
            draw.green.polygon(it)
        }
        draw.green.lightness(0.3).alpha(0.5).circle(mouse.world, selectionRadius)
        vertices.fori {
            draw.green.lightness(0.3).circle(it, selectionRadius)
        }
        triangleVertices.fori {
            draw.red.lightness(0.65).circle(it, selectionRadius)
        }

        farthestVertex?.let {
            draw.cyan.lightness(0.65).circle(it, selectionRadius)
        }

    }

    private fun handleInput() {
        var focusedIndex = -1
        for (i in vertices.indices) {
            val v = vertices[i]
            val distance = (vec(v) - vec(mouse.world)).r
            if (distance < 2*selectionRadius) {
                focusedIndex = i
                break
            }
        }

        if (focusedIndex == -1) {
            if (mouse.MB1.hasBeenPressed) {
                vertices.add(vec(mouse.world))
                list.add(vec(mouse.world))
                refresh()
            }
        } else {
            if (mouse.MB1()) {
                vertices[focusedIndex] = vec(mouse.world)
                refresh()
            }
        }

        if (mouse.MB2.hasBeenPressed) {
            if (focusedIndex >= 0) {
                vertices.remove(vertices[focusedIndex])
                refresh()
            }
        }
    }

    private val Vector.prev: Vector
        get() = vertices[vertices.indexLooped(vertices.indexOf(this) - 1)]
    private val Vector.next: Vector
        get() = vertices[vertices.indexLooped(vertices.indexOf(this) + 1)]

    private fun refresh() {
        createDiagonal()
        createPolygon()
        if (vertices.size > 4) {
            diagonalTriangulator = DiagonalTriangulator(vertices)
            diagonalTriangulator?.triangulate()
        }
    }

    private fun createDiagonal() {
        if (vertices.size > 2) {
            diagonal = calculateDiagonal()
        }
    }

    private fun createPolygon() {
        if (vertices.size > 2) {
            val array = vertices.toTypedArray()
            model = PolygonModel(array, triangulator = DiagonalTriangulator(vertices))
        }
    }

    private fun calculateDiagonal(): LineSegment {
        val leftMost = leftMost()
        val line = LineSegment.invoke(leftMost.prev, leftMost.next)

        val vertex = getCorrectVertexInTriangle(leftMost, line)

        return if (vertex == null) {
            line
        } else {
            LineSegment.invoke(leftMost, vertex)
        }
    }

    private fun leftMost(): Vector {
        var leftMost: Vector = vertices.first()
        vertices.fori {
            if (it.x < leftMost.x) {
                leftMost = it
            }
        }
        return leftMost
    }

    private fun getCorrectVertexInTriangle(leftMost: Vector, line: LineSegment): Vector? {
        var maxDistance = 0.0
        var vertex: Vector? = null
        triangleVertices.clear()
        vertices.fori {
            if (it.isInTriangle(leftMost, line.p1, line.p2)) {
                triangleVertices.add(it)
                val perpendicularDistance = abs(line.perpendicularDistance(it))
                if (perpendicularDistance > maxDistance) {
                    maxDistance = perpendicularDistance
                    vertex = it
                }
            }
        }

        farthestVertex = vertex
        return vertex
    }
}