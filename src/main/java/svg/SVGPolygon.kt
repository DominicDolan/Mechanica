package svg

import graphics.Polygon
import compatibility.Vector
import models.Model
import org.jsoup.nodes.Element

class SVGPolygon(element: Element) : SVGElement(element) {
    private val polygon: Polygon = Polygon(loadPolygonCoordinatesAsIs(element))
    val path: List<Vector>
        get() = polygon.path
    val centroid: Vector
        get() = polygon.centroid
    val model: Model
        get() = polygon.model

    fun adjust(
            toOrigin: Boolean = false,
            keepAspectRatio: Boolean = true,
            width: Double = 0.0,
            height: Double = 0.0) {

        polygon.adjust(toOrigin, keepAspectRatio, width, height)
    }
}