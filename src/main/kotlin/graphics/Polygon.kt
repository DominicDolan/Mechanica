package graphics

import geometry.triangulation.GrahamScanTriangulator
import util.extensions.toFloatArray
import util.units.LightweightVector
import util.units.Vector

interface Polygon : Iterable<Vector> {
    val path: List<Vector>
    val vertices: FloatArray
    val indices: ShortArray

    override fun iterator(): Iterator<Vector> {
        return path.iterator()
    }

    companion object {
        fun create(path: List<LightweightVector>): Polygon {
            return object : Polygon {
                private val triangulator = GrahamScanTriangulator(path.toTypedArray())
                override val path: List<Vector> = path
                override val vertices: FloatArray = path.toFloatArray()
                override val indices = triangulator.triangulate()
            }
        }
    }
}