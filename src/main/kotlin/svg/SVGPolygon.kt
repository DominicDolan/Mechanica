@file:Suppress("unused") // There will be many functions here that go unused most of the time
package svg

import gl.utils.IndexedVertices
import graphics.Polygon
import graphics.SolidPolygon
import models.Model
import org.jsoup.nodes.Element
import util.extensions.calculateLength
import util.units.Vector
import java.nio.FloatBuffer

class SVGPolygon(element: Element) : SVGElement(element), Polygon {
    private val polygon: SolidPolygon = SolidPolygon(loadPolygonCoordinates(element))
    override val path: List<Vector>
        get() = polygon.path
    override val indexedVertices: IndexedVertices
        get() = polygon.indexedVertices
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