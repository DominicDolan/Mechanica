package geometry.triangulation

import geometry.rectangleArea
import util.units.LightweightVector
import util.units.Vector

class TriangulatorList(path: Array<LightweightVector>) : Iterable<Vector>{
    val head: Node

    val ccw: Boolean

    private val allVertices = ArrayList<Node>()
    private val uncutVertices: TriangulatorListIterator
    private val concaveVertices: TriangulatorListIterator

    init {
        head = if (path.isNotEmpty()) addNode(path[0]) else zeroNode

        ccw = addAllFromPath(path)

        createLinkedList()

        uncutVertices = TriangulatorListIterator.VertexLoopIterator(head)
        concaveVertices = TriangulatorListIterator.ConcaveVertexIterator(null)

        createConcaveList()

    }

    fun removeLink(node: Node) {
        node.next.prev = node.prev
        node.prev.next = node.next
    }

    fun removeConcaveLink(node: Node) {
        node.prevConcave?.nextConcave = node.nextConcave
        node.nextConcave?.prevConcave = node.prevConcave
    }

    fun rewind() {
        createLinkedList()
        createConcaveList()
    }

    private fun addAllFromPath(path: Array<LightweightVector>):  Boolean {
        var totalArea = 0.0
        for (i in 1 until path.size) {
            addNode(path[i])

            if (i > 0) totalArea += calculateLineArea(path[i - 1], path[i])
        }
        return totalArea < 0.0
    }

    private fun addNode(vector: LightweightVector): Node {
        val n = Node(vector)
        n.listIndex = allVertices.size
        allVertices.add(n)
        return n
    }

    private fun createLinkedList() {
        if (allVertices.isNotEmpty()) {
            var prev = head
            for (i in 1 until allVertices.size) {
                val n = allVertices[i]

                n.prev = prev
                prev.next = n

                val nextIndex = if (i+1 < allVertices.size) i+1 else 0
                val area = rectangleArea(n, n.prev, allVertices[nextIndex])
                if (area == 0.0) continue

                prev = n
            }
            prev.next = head
            head.prev = prev
        }
    }

    private fun createConcaveList() {
        var current: Node? = null
        while (uncutVertices.hasNext()) {
            val node = uncutVertices.next()

            if (node.isConcave) {
                val n = current
                if (n == null) {
                    concaveVertices.setNewHead(node)
                } else {
                    n.nextConcave = node
                    node.prevConcave = n
                }
                current = node
            }
        }
    }

    override fun iterator() = uncutVertices

    inner class Node(vector: LightweightVector): Vector {
        override var x: Double = vector.x
        override var y: Double = vector.y

        var prev = head
        var next = head
        var nextConcave: Node? = null
        var prevConcave: Node? = null

        val isCCW: Boolean
            get() = ccw
        val isEar: Boolean
            get() = isEar(concaveVertices)
        val isConcave: Boolean
            get() = isConcave()
        var listIndex = -1

        override fun toString(): String {
            return "($x, $y)"
        }
    }
}