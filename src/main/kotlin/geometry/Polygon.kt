package geometry

import geometry.triangulation.TriangulatorList
import util.extensions.vec
import util.units.LightweightVector
import util.units.Vector

class Polygon(vertices: Array<LightweightVector>) {
    private val triangulatorList = TriangulatorList(vertices)
    private val lines = ArrayList<LineSegment>()

    private fun fillLines() {
        for (n in triangulatorList) {
            createLine(n.listIndex, n, n.next)
        }
    }

    private fun createLine(index: Int, p1: Vector, p2: Vector): LineSegment {
        while (lines.size <= index) {
            lines.add(unitLine)
        }
        val line = lines[index]
        line.p1.x = p1.x
        line.p1.y = p1.y
        line.p2.x = p2.x
        line.p2.y = p2.y

        return line
    }

    fun add(vector: LightweightVector) {

    }

    fun add(vector: LightweightVector, index: Int) {

    }



    operator fun get(index: Int) {

    }

    operator fun set(index: Int, value: LightweightVector) {

    }

    companion object {
        val unitLine: LineSegment
            get() = LineSegment(vec(0, 0), vec(1, 0))
    }
}