package gl.models

import geometry.lines.PolygonLine
import util.extensions.vec
import util.units.LightweightVector

class DynamicPolygonModel(vertices: Array<LightweightVector>)
    : PolygonModel(vertices)
{

    constructor(vertices: List<LightweightVector>) : this(vertices.toTypedArray())

    fun add(vector: LightweightVector) = add(vector, triangulator.vertices.size)

    fun add(vector: LightweightVector, index: Int): Boolean {
        val success = addLine(index, vector)

        if (success) {
        }
        triangulator.triangulate()
        updateBuffers()

        return success
    }

    operator fun set(index: Int, value: LightweightVector): Boolean {
        val success = setLine(index, value)

        if (success) {
        }
            triangulator.indexCount = 0
            triangulator.triangulate()
            updateBuffers()

        return success
    }

    private fun addLine(index: Int, new: LightweightVector): Boolean {
        val n = triangulator.add(index, new)
        lines.add(PolygonLine(triangulator.vertices, n.listIndex))

        if (canTriangulate(index)) {
            //revert
            return false
        }
        return true
    }

    private fun setLine(index: Int, new: LightweightVector): Boolean {
        val line = lines[index]

        val old = vec(line.p1)

        val previous = line.previous
        line.setP1(new, previous)

        if (canTriangulate(index)) {
            line.setP1(old, previous)
            return false
        }
        return true
    }

    private fun canTriangulate(changedIndex: Int): Boolean {
        val line = lines[changedIndex]
        line.checkLineIsValid(changedIndex)

        val previous = line.previous

        return !(line.intersectsAny() || previous.intersectsAny())
    }

    private fun PolygonLine.checkLineIsValid(index: Int) {
        if (this.listIndex != index)
            throw IllegalStateException("<PolygonLine.listIndex> and the its corresponding index in the lines array must always be the same")
    }

    private fun PolygonLine.intersectsAny(): Boolean {
        for (line in lines) {
            if (line === this || line === this.previous || line === this.next) {
                continue
            }
            if (this.segmentIntersect(line)) {
                return true
            }
        }
        return false
    }

    private val PolygonLine.previous: PolygonLine
        get() {
            val index = if (this.listIndex == 0) lines.lastIndex else this.listIndex - 1
            return lines[index]
        }

    private val PolygonLine.next: PolygonLine
        get() {
            val index = if (this.listIndex == lines.lastIndex) 0 else this.listIndex + 1
            return lines[index]
        }

}