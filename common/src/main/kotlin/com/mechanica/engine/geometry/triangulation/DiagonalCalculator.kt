package com.mechanica.engine.geometry.triangulation

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.geometry.isInTriangle
import com.mechanica.engine.geometry.lines.LineSegment
import com.mechanica.engine.util.extensions.fori
import com.mechanica.engine.util.extensions.indexLooped
import kotlin.math.abs

class DiagonalCalculator(private val vertices: ArrayList<Vector2>) {

    private val Vector2.prev: Vector2
        get() = vertices[vertices.indexLooped(vertices.indexOf(this) - 1)]
    private val Vector2.next: Vector2
        get() = vertices[vertices.indexLooped(vertices.indexOf(this) + 1)]

    private var p1Index: Int = -1
    private var p2Index: Int = -1

    val diagonal = calculateDiagonal()

    val subPolygon1 = ArrayList<Vector2>()
    val subPolygon2 = ArrayList<Vector2>()


    init {
        createSubPolygons()
    }

    private fun calculateDiagonal(): LineSegment {
        val leftMost = leftMost()
        val line = LineSegment.invoke(leftMost.prev, leftMost.next)
        p1Index = vertices.indexOf(leftMost.prev)
        p2Index = vertices.indexOf(leftMost.next)
        val vertex = getCorrectVertexInTriangle(leftMost, line)

        return if (vertex == null) {
            line
        } else {
            p1Index = vertices.indexOf(leftMost)
            p2Index = vertices.indexOf(vertex)
            LineSegment.invoke(leftMost, vertex)
        }
    }

    private fun createSubPolygons() {
        subPolygon1.addFromVertices(p1Index, p2Index)
        subPolygon2.addFromVertices(p2Index, p1Index)
    }

    private fun ArrayList<Vector2>.addFromVertices(fromIndex: Int, toIndex: Int) {
        var start = vertices[fromIndex]
        this.add(start)
        while (start !== vertices[toIndex]) {
            start = start.next
            this.add(start)
        }
    }

    private fun leftMost(): Vector2 {
        var leftMost: Vector2 = vertices.first()
        vertices.fori {
            if (it.x < leftMost.x) {
                leftMost = it
            }
        }
        return leftMost
    }

    private fun getCorrectVertexInTriangle(leftMost: Vector2, line: LineSegment): Vector2? {
        var maxDistance = 0.0
        var vertex: Vector2? = null
        vertices.fori {
            if (it.isInTriangle(leftMost, line.p1, line.p2)) {
                val perpendicularDistance = abs(line.perpendicularDistance(it))
                if (perpendicularDistance > maxDistance) {
                    maxDistance = perpendicularDistance
                    vertex = it
                }
            }
        }

        return vertex
    }
}