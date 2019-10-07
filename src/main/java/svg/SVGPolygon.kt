package svg

import compatibility.Vector
import loader.loadTriangulatedModel
import models.Model
import org.jsoup.nodes.Element
import util.extensions.centroid

class SVGPolygon(element: Element) : SVGElement(element) {
    val path: List<Vector> = loadPolygonCoordinatesAsIs(element)
    val centroid: Vector = path.centroid()
    var model: Model? = null
        get() {
            if (field == null) {
                field = loadTriangulatedModel(path)
            }
            return field
        }
        private set

    fun adjust(
            toOrigin: Boolean = false,
            keepAspectRatio: Boolean = true,
            width: Double = 0.0,
            height: Double = 0.0) {

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

        model = loadTriangulatedModel(path)

    }
}