package geometry.triangulation

class TriangulatorListIterator {
    class VertexLoopIterator(var head: TriangulatorList.Node) : Iterator<TriangulatorList.Node> {
        private var current = head
        private var loopHasStarted = false

        override fun hasNext(): Boolean {
            val hasNext = current.listIndex != 0 || !loopHasStarted
            loopHasStarted = true
            if (!hasNext) {
                loopHasStarted = false
                current = head
            }
            return hasNext
        }

        override fun next(): TriangulatorList.Node {
            val cursor = current
            current = current.next
            return cursor
        }

        fun setNewHead(head: TriangulatorList.Node) {
            this.head = head
            current = head
        }
    }

    class ConcaveVertexIterator(private var head: TriangulatorList.Node?) : Iterator<TriangulatorList.Node> {
        private var current = head
        override fun hasNext(): Boolean {
            val hasNext = current != null
            if (!hasNext) {
                current = head
            }
            return hasNext
        }

        override fun next() = nextNode()

        fun nextNode(): TriangulatorList.Node {
            val cursor = current
            current = current?.nextConcave
            return cursor ?: throw IllegalStateException("Cannot iterate over null values")
        }


        fun setNewHead(head: TriangulatorList.Node?) {
            this.head = head
            current = head
        }
    }

}