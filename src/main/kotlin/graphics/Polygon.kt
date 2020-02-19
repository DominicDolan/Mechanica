package graphics

import gl.utils.IndexedVertices
import loader.loadTriangulatedArrays
import loader.loadTriangulatedModel
import models.Model
import util.units.Vector

interface Polygon : Iterable<Vector> {
    val path: List<Vector>
    val indexedVertices: IndexedVertices
    val model: Model

    override fun iterator(): Iterator<Vector> {
        return path.iterator()
    }

    companion object {
        fun create(path: List<Vector>): Polygon {
            return object : Polygon {
                override val path: List<Vector> = path
                override val indexedVertices = loadTriangulatedArrays(path)
                override val model: Model = loadTriangulatedModel(path)

            }
        }
    }
}