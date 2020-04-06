package geometry.triangulation

import geometry.LineSegment
import util.units.Vector
import kotlin.math.abs

class Triangulator(private val triangulatorList: TriangulatorList) {
    val indices = ShortArray(500)
    var indexCount = 0

    fun triangulate(indices: ShortArray = this.indices) {
        val list = triangulatorList
        indexCount = 0
        val start = getStart(list, indices) ?: return

        iterate(start, list, indices)
        triangulatorList.rewind()
    }

    private fun getStart(list: TriangulatorList, indices: ShortArray): TriangulatorList.Node? {
        val start = list.head.next.next
        if (start.next === list.head) {
            createTriangle(start, indices)
            return null
        }
        return start
    }

    private tailrec fun iterate(current: TriangulatorList.Node, list: TriangulatorList, indices: ShortArray): TriangulatorList.Node {
        val new = if (current.isEar && !hasBecomeTriangle(current)) {
            createTriangle(current, indices)
            removeLinks(current, list)
        } else {
            current.next
        }
        return if (new != list.head) iterate(new, list, indices) else new
    }

    private fun hasBecomeTriangle(current: TriangulatorList.Node) = (current === current.next.next)

    private fun removeLinks(current: TriangulatorList.Node, list: TriangulatorList): TriangulatorList.Node {
        list.removeLink(current)
        var new = current.prev
        if (!new.isConcave) {
            list.removeConcaveLink(current)
        }
        if (!new.prev.isConcave) {
            list.removeConcaveLink(current)
        }
        if (new.prev.listIndex == 0) {
            new = current.next
        }
        return new
    }

    val lines = ArrayList<LineSegment>()
    private fun createTriangle(node: TriangulatorList.Node, indices: ShortArray) {
        indices[indexCount] = node.prev.listIndex.toShort()
        indices[indexCount + 1] = node.listIndex.toShort()
        indices[indexCount + 2] = node.next.listIndex.toShort()

        indexCount+=3
        lines.add(LineSegment(node.prev, node.next))
    }
}