package graphics

import gl.utils.IndexedVertices
import loader.loadTriangulatedArrays
import util.extensions.*
import util.units.Vector

class SolidPolygon(override val path: List<Vector>) : Polygon {
    override val indexedVertices: IndexedVertices = loadTriangulatedArrays(path)

    fun adjust(
            toOrigin: Boolean,
            keepAspectRatio: Boolean,
            width: Double,
            height: Double) {

        if (toOrigin) {
            path.toOrigin()
        }

        if (keepAspectRatio) {
            if (width != 0.0) {
                path.normalizeX(width)
            }
            if (height != 0.0) {
                path.normalizeY(height)
            }
        } else {
            if (width != 0.0) {
                val currentWidth = path.xRange()
                val factor = width/currentWidth
                path.scaleX(factor)
            }
            if (height != 0.0) {
                val currentHeight = path.yRange()
                val factor = height/currentHeight
                path.scaleY(factor)
            }
        }

    }
}