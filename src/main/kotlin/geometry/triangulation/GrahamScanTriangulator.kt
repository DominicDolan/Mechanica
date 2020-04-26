package geometry.triangulation

import geometry.lines.LineSegment
import geometry.triangulation.iterators.ConcaveVertexIterable
import geometry.triangulation.iterators.TriangulatorIterable
import geometry.triangulation.iterators.VertexLoopIterable
import util.extensions.vec
import util.units.LightweightVector
import util.units.Vector

class GrahamScanTriangulator(path: Array<LightweightVector>) : Triangulator(path) {
    override val indices = ShortArray(500)
    override var indexCount = 0
    var current: Node? = null
    var new: Node? = null

    private val head: Node
        get() = vertices.first()

    private val activeVertices: TriangulatorIterable
    override val concaveVertices: TriangulatorIterable

    init {
        activeVertices = VertexLoopIterable(vertices)
        concaveVertices = ConcaveVertexIterable(vertices)
    }

    override fun triangulate(): ShortArray {
        indexCount = 0
        val start = getStart() ?: return indices

        iterate(start)
        rewind()
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
        if (new.prev.listIndex == 0) {
            new = current.next
        }
        return new
    }

    val lines = ArrayList<LineSegment>()
    private fun createTriangle(node: Node, indices: ShortArray) {
        indices[indexCount] = node.prev.listIndex.toShort()
        indices[indexCount + 1] = node.listIndex.toShort()
        indices[indexCount + 2] = node.next.listIndex.toShort()

        indexCount+=3
        lines.add(LineSegment(node.prev, node.next))
    }

    override fun rewind() {
        activeVertices.rewind()
        concaveVertices.rewind()
    }

}