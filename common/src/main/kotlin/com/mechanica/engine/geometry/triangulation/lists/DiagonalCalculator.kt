package com.mechanica.engine.geometry.triangulation.lists

import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.geometry.isInTriangle
import com.mechanica.engine.geometry.lines.LineSegment
import kotlin.math.abs

private typealias Node = TriangulatorList<Vector2>.Node
class DiagonalCalculator(private val list: TriangulatorList<Vector2>) : LineSegment() {

    private lateinit var activeHead: Node

    override lateinit var p1: Node
        private set
    override lateinit var p2: Node
        private set

    var leftMostTemp: Vector2 = vec(0.0, 0.0)
    var furthestTemp: Vector2? = null

    fun calculate(head: Node): LineSegment {
        activeHead = head
        calculateDiagonal()
        return this
    }

    fun setState(p1: Node, p2: Node, head: Node) {
        this.p1 = p1
        this.p2 = p2
        this.activeHead = head
    }

    private fun calculateDiagonal() {
        val leftMost = leftMost()

        p1 = leftMost.prev
        p2 = leftMost.next

        val vertex = getCorrectVertexInTriangle(leftMost)

        if (vertex != null) {
            p1 = leftMost
            p2 = vertex
        }
    }

    private fun leftMost(): Node {
        var leftMost: Node = activeHead
        list.loopFrom(activeHead) {
            if (it.x < leftMost.x) {
                leftMost = it
            }
        }
        this.leftMostTemp = leftMost
        return leftMost
    }

    private fun getCorrectVertexInTriangle(leftMost: Vector2): Node? {
        var maxDistance = 0.0
        var vertex: Node? = null
        list.loopFrom(activeHead) {
            if (it.isInTriangle(leftMost, p1, p2)) {
                val perpendicularDistance = abs(perpendicularDistance(it))
                if (perpendicularDistance > maxDistance) {
                    maxDistance = perpendicularDistance
                    vertex = it
                }
            }
        }

        furthestTemp = vertex
        return vertex
    }

    operator fun component1() = p1
    operator fun component2() = p2
    operator fun component3() = activeHead
}