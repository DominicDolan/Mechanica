package geometry.triangulation

import game.Game
import geometry.LineSegment
import geometry.isLeftOf
import util.extensions.vec
import util.units.LightweightVector

class TriangulatorList(path: Array<LightweightVector>) : Iterable<TriangulatorList.Node>,Iterator<TriangulatorList.Node>{
    val head: Node
    var current: Node

    var concaveHead: Node? = null

    private val ccw: Boolean

    init {
        head = Node(coordinateToLine(path, 0))
        current = this.head

        ccw = pathToLinkedList(head, path)
        createConcaveList()
    }

    override fun hasNext() = (current.next !== head).also { println("has next: $it") }

    override fun next(): Node {
        val cursor = current
        current = current.next
        return cursor
    }

    private fun Array<LightweightVector>.createNode(index: Int) = Node(coordinateToLine(this, index))

    private fun createConcaveList() {
        var current: Node? = null
        for (node in iterator()) {
            if (node.isConcave) {
                val n = current
                if (n == null) {
                    concaveHead = node
                } else {
                    n.next = node
                }
                current = node
            }
        }
    }

    override fun iterator() = this

    inner class Node(val line: LineSegment) {
        var prev = head
        var next = head
        var nextConcave: Node? = null
        var isEar = false

        var isLeft = false
        val isConcave: Boolean
            get() = isLeft && !ccw

        override fun toString(): String {
            return line.toString()
        }
    }

    companion object {

        private fun TriangulatorList.pathToLinkedList(head: Node, path: Array<LightweightVector>): Boolean {
            var ccw = true
            if (path.isNotEmpty()) {
                var prev = head
                var area = 0.0
                for (i in 1 until path.size) {
                    area += calculateLineArea(path[i - 1], path[i])
                    val newNode = path.createNode(i)

                    prev.next = newNode
                    newNode.prev = prev

                    newNode.isLeft = newNode.line.p2.isLeftOf(prev.line)

                    prev = newNode
                }

                ccw = area < 0.0

                prev.next = head
                head.prev = prev
            }
            return ccw
        }

        private fun coordinateToLine(path: Array<LightweightVector>, index: Int): LineSegment {
            val check = checkCoordinatesForLineConversion(path, index)
            if (check != null) return check

            val p1 = path[index]
            val secondIndex = if (index + 1 == path.size) 0
            else index + 1
            val p2 = path[secondIndex]

            return LineSegment(p1, p2)

        }


        private fun checkCoordinatesForLineConversion(path: Array<LightweightVector>, index: Int): LineSegment? {
            if (path.isNotEmpty()) {
                return if (path.size > 1 && index < path.size) {
                    null
                } else {
                    if (Game.debug.printWarnings) {
                        System.err.println("The size of this path is not big enough to make a line or the index supplied is larger than the path size")
                        System.err.println(Exception().stackTrace?.contentToString())
                    }
                    LineSegment(path[0], path[0])
                }
            } else {
                if (Game.debug.printWarnings) {
                    System.err.println("An empty path was supplied so a point line at the origin is being returned")
                    System.err.println(Exception().stackTrace?.contentToString())
                }
                return LineSegment(vec(0.0, 0.0), vec(0.0, 0.0))
            }
        }

        private fun calculateLineArea(p1: LightweightVector, p2: LightweightVector): Double {
            return (p2.x - p1.x)*(p2.y + p1.y)
        }

    }
}