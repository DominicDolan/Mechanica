package com.mechanica.engine.geometry.triangulation.polygon

import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.geometry.triangulation.Triangulator
import com.mechanica.engine.geometry.triangulation.lists.DiagonalCalculator
import com.mechanica.engine.geometry.triangulation.lists.TriangulatorList

private typealias Node = TriangulatorList<Vector2>.Node
class DiagonalTriangulator(vertices: List<Vector2>) : Triangulator {

    override var indices: ShortArray = ShortArray(100)
        private set
    override var indexCount: Int = 0
        private set

    val list = TriangulatorList(vertices)
    val diagonalCalculator = DiagonalCalculator(list)

    override fun triangulate(): ShortArray {
        val head = list.head
        if (head != null) {
            if (head.next === head) return indices
            calculateDiagonals(head)
        }
        return indices
    }

    private fun calculateDiagonals(node: Node) {
        if (node.next.next.next === node) {
            storeVertices(node)
            return
        }

        diagonalCalculator.calculate(node)

        splitForwardAndCalculate(diagonalCalculator)
        splitBackwardAndCalculate(diagonalCalculator)

    }

    private fun splitForwardAndCalculate(diagonal: DiagonalCalculator) {
        val (p1, p2, head) = diagonal
        splitAndCalculateDiagonal(diagonal.p1, diagonal.p2)
        diagonal.setState(p1, p2, head)
    }

    private fun splitBackwardAndCalculate(diagonal: DiagonalCalculator) {
        val (p1, p2, head) = diagonal
        splitAndCalculateDiagonal(diagonal.p2, diagonal.p1)
        diagonal.setState(p1, p2, head)
    }

    private fun splitAndCalculateDiagonal(p1: Node, p2: Node) {

        val oldP2Next = p2.next
        val oldP1Prev = p1.prev

        // Split path
        p2.linkTo(p1)

        calculateDiagonals(p1)

        //Reset the split
        p2.linkTo(oldP2Next)
        oldP1Prev.linkTo(p1)

    }

    private fun storeVertices(node: Node) {
        if (indexCount + 3 >= indices.size) resizeIndicesArray()

        indices[indexCount++] = node.index.toShort()
        indices[indexCount++] = node.next.index.toShort()
        indices[indexCount++] = node.next.next.index.toShort()
    }

    private fun resizeIndicesArray() {
        val size = indices.size
        indices = ShortArray(size*3) { if (it < size) indices[it] else 0.toShort() }
    }

}