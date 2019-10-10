package graphics

import compatibility.Vector
import loader.loadTriangulatedModel
import models.Model
import util.extensions.centroid

interface Polygon {
    val path: List<Vector>
    val centroid: Vector
    val model: Model

    fun adjust(
            toOrigin: Boolean = false,
            keepAspectRatio: Boolean = true,
            width: Double = 0.0,
            height: Double = 0.0)
}