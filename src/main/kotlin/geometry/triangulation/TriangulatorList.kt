package geometry.triangulation

import drawer.Drawer
import geometry.rectangleArea
import geometry.triangulation.iterators.ConcaveVertexIterable
import geometry.triangulation.iterators.TriangulatorIterable
import geometry.triangulation.iterators.VertexLoopIterable
import util.units.LightweightVector
import util.units.Vector

class TriangulatorList(path: Array<LightweightVector>) {
    val head: Node

    val ccw: Boolean

    val allVertices = ArrayList<Node>()
    val uncutVertices: TriangulatorIterable
    val concaveVertices: TriangulatorIterable

    init {
        head = if (path.isNotEmpty()) addNode(path[0]) else zeroNode

        ccw = addAllFromPath(path)

        createLinkedList()

        uncutVertices = VertexLoopIterable(head)
        concaveVertices = ConcaveVertexIterable(null)

        createConcaveList()

    }

    fun render(draw: Drawer) {
        for (n in allVertices) {
            if (n.isConcave) {
                draw.yellow
            }
            else {
//                if (n.isEar) {
//                    draw.blue
//                } else {
//                    draw.grey
//                }
            }
            draw.circle(n, 0.02)
        }
    }

    fun removeLink(node: Node) {
        node.next.prev = node.prev
        node.prev.next = node.next
    }

    fun removeConcaveLink(node: Node) {
        node.prevConcave?.nextConcave = node.nextConcave
        node.nextConcave?.prevConcave = node.prevConcave
    }

    fun add(vector: LightweightVector): Node {
        val n = addNode(vector)
        rewind()
        return n
    }

    fun add(index: Int, vector: LightweightVector): Node {
        val n = addNode(vector, index)
        rewind()
        return n
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

    private fun addNode(vector: LightweightVector, index: Int = allVertices.size): Node {
        val n = Node(vector)
        n.listIndex = index
        allVertices.add(index, n)
        return n
    }

    private fun createLinkedList() {
        if (allVertices.isNotEmpty()) {
            var prev = head
            for (i in 1 until allVertices.size) {
                val n = allVertices[i]
                n.listIndex = i

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
        for (v in allVertices) {
            if (v.isConcave) {
                val n = current
                if (n == null) {
                    concaveVertices.setNewHead(v)
                } else {
                    n.nextConcave = v
                    v.prevConcave = n
                }
                current = v
            }
        }
    }

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