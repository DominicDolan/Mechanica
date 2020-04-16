package geometry.triangulation.iterators

import geometry.triangulation.TriangulatorList

class VertexLoopIterable(private var head: TriangulatorList.Node) : TriangulatorIterable {
    private val iterator = VertexLoopIterator()
    override fun setNewHead(head: TriangulatorList.Node?) {
        if (head != null) {
            this.head = head
            iterator.current = head
        }
    }

    override fun iterator(): Iterator<TriangulatorList.Node> {
        iterator.lastIndex = -1
        iterator.current = head
        return iterator
    }

    private inner class VertexLoopIterator: Iterator<TriangulatorList.Node> {
        var current = head
        var lastIndex = -1

        override fun hasNext() = current.listIndex > lastIndex

        override fun next(): TriangulatorList.Node {
            val cursor = current
            lastIndex = current.listIndex
            current = current.next
            return cursor
        }
    }
}