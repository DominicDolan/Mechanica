package geometry

import geometry.triangulation.Triangulator
import geometry.triangulation.TriangulatorList
import gl.models.Model
import gl.vbo.AttributeArray
import gl.vbo.ElementIndexArray
import gl.vbo.pointer.VBOPointer
import org.lwjgl.opengl.GL40.*
import util.extensions.fill
import util.extensions.vec
import util.units.LightweightVector
import util.units.Vector

class PolygonModel(vertices: Array<LightweightVector>)
    :Model(
        AttributeArray(vertices.size*2, VBOPointer.position),
        ElementIndexArray(vertices.size*6),
        draw = {
            glDrawElements(GL_TRIANGLES, it.vertexCount, GL_UNSIGNED_SHORT, 0)
        })
{

    private var pathVertexCount = vertices.size
    private var floats: FloatArray
    private val positions = (inputs[0] as AttributeArray)
    private val indices = (inputs[1] as ElementIndexArray)

    private val triangulatorList = TriangulatorList(vertices)
    private val triangulator = Triangulator(triangulatorList)
    private val lines = ArrayList<LineSegment>()

    constructor(vertices: List<LightweightVector>) : this(vertices.toTypedArray())

    init {
        floats = FloatArray(pathVertexCount*3)
        fillFloats(triangulatorList)
        fillLines()
        triangulator.triangulate()

        indices.update(triangulator.indices)

        vertexCount = triangulator.indexCount
    }

    private fun fillLines() {
        for (n in triangulatorList) {
            createLine(n.listIndex, n, n.next)
        }
    }

    private fun fillFloats(path: TriangulatorList) {
        if (pathVertexCount*3 > floats.size) {
            floats = FloatArray(floats.size*2)
        }

        floats.fill(path.iterator())
        positions.update(floats)
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