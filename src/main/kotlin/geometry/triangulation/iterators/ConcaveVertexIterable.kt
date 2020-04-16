package geometry.triangulation.iterators

import geometry.triangulation.TriangulatorList

class ConcaveVertexIterable(private var head: TriangulatorList.Node?) : TriangulatorIterable {
    private val iterator = ConcaveVertexIterator()
    override fun setNewHead(head: TriangulatorList.Node?) {
        this.head = head
        iterator.current = head
    }

    override fun iterator(): Iterator<TriangulatorList.Node> {
        iterator.current = head
        return iterator
    }

    private inner class ConcaveVertexIterator : Iterator<TriangulatorList.Node> {
        var current = head

        override fun hasNext(): Boolean = current != null

        override fun next(): TriangulatorList.Node {
            val cursor = current
            current = current?.nextConcave
            return cursor ?: throw IllegalStateException("Cannot iterate over null values")
        }
    }
}