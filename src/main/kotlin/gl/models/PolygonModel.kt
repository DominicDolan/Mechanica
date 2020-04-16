package gl.models

import geometry.lines.LineSegment
import geometry.lines.PolygonLine
import geometry.triangulation.Triangulator
import geometry.triangulation.TriangulatorList
import gl.vbo.AttributeArray
import gl.vbo.ElementIndexArray
import gl.vbo.pointer.VBOPointer
import org.lwjgl.opengl.GL40
import util.extensions.fill
import util.extensions.vec
import util.units.LightweightVector

open class PolygonModel(vertices: Array<LightweightVector>)
:Model(
    AttributeArray(vertices.size*2, VBOPointer.position),
    ElementIndexArray(vertices.size*6),
    draw = {
        GL40.glDrawElements(GL40.GL_TRIANGLES, it.vertexCount, GL40.GL_UNSIGNED_SHORT, 0)
    })
{
    protected val triangulatorList = TriangulatorList(vertices)
    protected val triangulator = Triangulator(triangulatorList)

    private var floats: FloatArray
    private val positions = (inputs[0] as AttributeArray)
    private val indices = (inputs[1] as ElementIndexArray)
    val lines = ArrayList<PolygonLine>()

    constructor(vertices: List<LightweightVector>) : this(vertices.toTypedArray())

    init {
        fillLines()
        floats = FloatArray(lines.size*3) { 0f }
        triangulator.triangulate()

        updateBuffers()
        vertexCount = triangulator.indexCount
    }

    protected fun updateBuffers() {
        vertexCount = triangulator.indexCount
        indices.update(triangulator.indices)
        fillFloats(triangulatorList)
    }

    private fun fillFloats(path: TriangulatorList) {
        if (path.allVertices.size*3 > floats.size) {
            floats = FloatArray(path.allVertices.size*6)
        }

        floats.fill(path.allVertices)
        positions.update(floats)
    }

    private fun fillLines() {
        for (n in triangulatorList.allVertices) {
            createLine(n)
        }
    }

    protected fun addLine() {

    }

    private fun createLine(node: TriangulatorList.Node): LineSegment {
        val index = node.listIndex
        when {
            lines.size < index -> {
                throw ArrayIndexOutOfBoundsException("Can't add a line because the lines list is not big enough to place the new line at <node.listIndex>")
            }
            index == lines.size -> {
                lines.add(PolygonLine(triangulatorList.allVertices, index))
            }
            else -> {
                lines[index] = PolygonLine(triangulatorList.allVertices, index)
            }
        }
        return lines[index]
    }

}