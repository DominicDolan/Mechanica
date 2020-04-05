package geometry.triangulation

import game.Game
import geometry.LineSegment
import geometry.rectangleArea
import geometry.isInTriangle
import geometry.isLeftOf
import util.extensions.vec
import util.units.LightweightVector
import util.units.Vector

class TriangulatorList(path: Array<LightweightVector>) : Iterable<TriangulatorList.Node>{
    val head: Node

    val ccw: Boolean

    private val allNodes = ArrayList<Node>()
    private val uncutLines: TriangulatorListIterator.VertexLoopIterator
    private val concaveIterator: TriangulatorListIterator.ConcaveVertexIterator
    val concaveList: Iterator<Node>
        get() = concaveIterator

    init {
        head = if (path.isNotEmpty()) addNode(path[0]) else zeroNode

        ccw = addAllFromPath(path)

        createLinkedList()

        uncutLines = TriangulatorListIterator.VertexLoopIterator(head)
        concaveIterator = TriangulatorListIterator.ConcaveVertexIterator(null)

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
        n.listIndex = allNodes.size
        allNodes.add(n)
        return n
    }

    private fun createLinkedList() {
        if (allNodes.isNotEmpty()) {
            var prev = head
            for (i in 1 until allNodes.size) {
                val n = allNodes[i]

                n.prev = prev
                prev.next = n

                val nextIndex = if (i+1 < allNodes.size) i+1 else 0
                val area = rectangleArea(n, n.prev, allNodes[nextIndex])
                if (area == 0.0) continue

                prev = n
            }
            prev.next = head
            head.prev = prev
        }
    }

    private fun createConcaveList() {
        var current: Node? = null
        while (uncutLines.hasNext()) {
            val node = uncutLines.next()

            if (node.isConcave) {
                val n = current
                if (n == null) {
                    concaveIterator.setNewHead(node)
                } else {
                    n.nextConcave = node
                    node.prevConcave = n
                }
                current = node
            }
        }
    }

    inline fun forEachEar(block: (Vector) -> Unit) {
        while (iterator().hasNext()) {
            val node = iterator().next()

            if (node.isEar) {
                block(node)
            }
        }
    }

    override fun iterator() = uncutLines

    inner class Node(vector: LightweightVector): Vector {
        override var x: Double = vector.x
        override var y: Double = vector.y

        var prev = head
        var next = head
        var nextConcave: Node? = null
        var prevConcave: Node? = null

        val isEar: Boolean
            get() = isEar(this)
        val isConcave: Boolean
            get() = isConcave(this)
        var listIndex = -1

        override fun toString(): String {
            return "($x, $y)"
        }
    }

    fun isEar(node: Node): Boolean {
        val p2 = node.next
        val p3 = node.prev
        for (n in concaveIterator) {
            if (n.isInTriangle(node, p2, p3)) return false
        }
        return !node.isConcave
    }

    fun isConcave(node: Node): Boolean {
        val area = rectangleArea(node.next, node.prev, node)
        val isLeft = area > 0.0
        return (isLeft && !ccw) || (!isLeft && ccw)
    }

    inner class PolygonLine(p1: Vector, p2: Vector) : LineSegment(p1, p2) {
        var isLeft = false
        val isConcave: Boolean
            get() = (isLeft && !ccw) || (!isLeft && ccw)

    }

    companion object {

        private val TriangulatorList.zeroNode
            get() = Node(vec(0, 0))

        private fun TriangulatorList.coordinateToLine(path: Array<LightweightVector>, index: Int): PolygonLine {
            val check = checkCoordinatesForLineConversion(path, index)
            if (check != null) return check

            val p1 = path[index]
            val secondIndex = if (index + 1 == path.size) 0
            else index + 1
            val p2 = path[secondIndex]

            return PolygonLine(p1, p2)

        }


        private fun TriangulatorList.checkCoordinatesForLineConversion(path: Array<LightweightVector>, index: Int): PolygonLine? {
            if (path.isNotEmpty()) {
                return if (path.size > 1 && index < path.size) {
                    null
                } else {
                    if (Game.debug.printWarnings) {
                        System.err.println("The size of this path is not big enough to make a line or the index supplied is larger than the path size")
                        System.err.println(Exception().stackTrace?.contentToString())
                    }
                    PolygonLine(path[0], path[0])
                }
            } else {
                if (Game.debug.printWarnings) {
                    System.err.println("An empty path was supplied so a point line at the origin is being returned")
                    System.err.println(Exception().stackTrace?.contentToString())
                }
                return PolygonLine(vec(0.0, 0.0), vec(0.0, 0.0))
            }
        }

        private fun calculateLineArea(p1: LightweightVector, p2: LightweightVector): Double {
            return (p2.x - p1.x)*(p2.y + p1.y)
        }

    }
}