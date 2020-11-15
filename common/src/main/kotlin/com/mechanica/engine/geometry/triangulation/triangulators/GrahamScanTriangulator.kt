package com.mechanica.engine.geometry.triangulation.triangulators

import com.mechanica.engine.geometry.lines.LineSegment
import com.mechanica.engine.geometry.triangulation.AbstractTriangulator
import com.mechanica.engine.geometry.triangulation.TriangulatorNode
import com.mechanica.engine.geometry.triangulation.isConcave
import com.mechanica.engine.geometry.triangulation.isEar
import com.mechanica.engine.geometry.triangulation.iterators.ConcaveVertexIterable
import com.mechanica.engine.geometry.triangulation.iterators.TriangulatorIterable
import com.mechanica.engine.geometry.triangulation.iterators.VertexLoopIterable
import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.util.extensions.indexLooped

// From: "The Graham scan triangulates simple polygons" Pattern Recognition Letters, 1990. Kong, X., H. Everett, and G.T. Toussaint
class GrahamScanTriangulator(path: Array<out Vector>) : AbstractTriangulator<GrahamScanTriangulator.Node>(path) {
    override val indices = ShortArray(500)
    override var indexCount = 0
    var current: Node? = null
    var new: Node? = null

    private val head: Node
        get() = vertices.first()

    private val activeVertices: TriangulatorIterable = VertexLoopIterable(vertices)
    private val concaveVertices: TriangulatorIterable = ConcaveVertexIterable(vertices)

    override fun createNode(vector: InlineVector): Node {
        return Node(vector)
    }

    override fun triangulate(): ShortArray {
        indexCount = 0
        val start = getStart() ?: return indices

        iterate(start)
        reorganise()
        return indices
    }

    private fun getStart(): Node? {
        val start = head.next.next
        if (start.next === head) {
            createTriangle(start, indices)
            return null
        }
        return start
    }

    private tailrec fun iterate(current: Node): Node {
        val new = processNode(current, indices)
        return if (new != head) iterate(new) else new
    }

    private fun processNode(current: Node, indices: ShortArray): Node {
        return if (current.isEar && !hasBecomeTriangle(current)) {
            createTriangle(current, indices)
            removeLinks(current)
        } else {
            current.next
        }
    }

    private fun hasBecomeTriangle(current: Node) = (current === current.next.next)

    private fun removeLinks(current: Node): Node {
        activeVertices.removeLink(current)
        var new = current.prev
        if (!new.isConcave) {
            concaveVertices.removeLink(current)
        }
        if (new.prev.index == 0) {
            new = current.next
        }
        return new
    }

    val lines = ArrayList<LineSegment>()
    private fun createTriangle(node: Node, indices: ShortArray) {
        indices[indexCount] = node.prev.index.toShort()
        indices[indexCount + 1] = node.index.toShort()
        indices[indexCount + 2] = node.next.index.toShort()

        indexCount += 3
        lines.add(LineSegment(node.prev, node.next))
    }

    override fun reorganise() {
        super.reorganise()
        if (activeVertices != null && concaveVertices != null) {
            activeVertices.rewind()
            concaveVertices.rewind()
        }
    }

    inner class Node(vector: Vector) : TriangulatorNode() {

        override var x: Double = vector.x
        override var y: Double = vector.y

        private val defaultPrevious: Node
            get() = vertices[vertices.indexLooped(index - 1)]
        private val defaultNext: Node
            get() = vertices[vertices.indexLooped(index + 1)]

        lateinit var prev: Node
        lateinit var next: Node
        var nextConcave: Node? = null
        var prevConcave: Node? = null

        val isEar: Boolean
            get() = isEar(concaveVertices)
        val isConcave: Boolean
            get() = isConcave(prev, this, next, ccw)

        fun rewind() {
            prev = defaultPrevious
            next = defaultNext
        }

        override fun toString(): String {
            return "($x, $y)"
        }
    }

}
