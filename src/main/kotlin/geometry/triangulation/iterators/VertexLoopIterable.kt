package geometry.triangulation.iterators

import geometry.rectangleArea
import geometry.triangulation.Triangulator

class VertexLoopIterable(private val list: ArrayList<Triangulator.Node>) : TriangulatorIterable {
    override val head: Triangulator.Node
        get() = list.first()

    private val iterator = VertexLoopIterator()

    init {
        rewind()
    }

    override fun rewind() {
        for (n in list) {
            n.rewind()
        }
    }

    override fun removeLink(node: Triangulator.Node) {
        node.next.prev = node.prev
        node.prev.next = node.next
    }

    override fun iterator(): Iterator<Triangulator.Node> {
        iterator.lastIndex = -1
        iterator.current = head
        return iterator
    }

    private inner class VertexLoopIterator: Iterator<Triangulator.Node> {
        var current = head
        var lastIndex = -1

        override fun hasNext() = current.listIndex > lastIndex

        override fun next(): Triangulator.Node {
            val cursor = current
            lastIndex = current.listIndex
            current = current.next
            return cursor
        }
    }
}