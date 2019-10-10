package graphics

import compatibility.Vector
import loader.loadTriangulatedModel
import models.Model
import util.extensions.*

class SolidPolygon(override val path: List<Vector>) : Polygon {
    override val centroid: Vector = path.centroid()
    private var modelInstance: Model? = null
    override val model: Model
        get() {
            val model: Model = modelInstance ?: loadTriangulatedModel(path)
            this.modelInstance = model
            return model
        }

    override fun adjust(
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

        modelInstance = loadTriangulatedModel(path)

    }
}