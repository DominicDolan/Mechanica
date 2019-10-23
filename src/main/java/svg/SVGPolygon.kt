package svg

import graphics.SolidPolygon
import compatibility.VectorConverter
import graphics.Polygon
import models.Model
import org.jsoup.nodes.Element
import util.extensions.calculateLength
import util.units.Vector

class SVGPolygon(element: Element) : SVGElement(element), Polygon {
    private val polygon: SolidPolygon = SolidPolygon(loadPolygonCoordinates(element))
    override val path: List<Vector>
        get() = polygon.path
    override val model: Model
        get() = polygon.model
    val pathLength: Double = path.calculateLength()

    fun adjust(
            toOrigin: Boolean,
            keepAspectRatio: Boolean,
            width: Double,
            height: Double) {

        polygon.adjust(toOrigin, keepAspectRatio, width, height)
    }
}