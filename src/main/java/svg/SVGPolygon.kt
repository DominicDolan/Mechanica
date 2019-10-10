package svg

import graphics.SolidPolygon
import compatibility.Vector
import graphics.Polygon
import models.Model
import org.jsoup.nodes.Element

class SVGPolygon(element: Element) : SVGElement(element), Polygon {
    private val polygon: SolidPolygon = SolidPolygon(loadPolygonCoordinatesAsIs(element))
    override val path: List<Vector>
        get() = polygon.path
    override val centroid: Vector
        get() = polygon.centroid
    override val model: Model
        get() = polygon.model

    override fun adjust(
            toOrigin: Boolean,
            keepAspectRatio: Boolean,
            width: Double,
            height: Double) {

        polygon.adjust(toOrigin, keepAspectRatio, width, height)
    }
}