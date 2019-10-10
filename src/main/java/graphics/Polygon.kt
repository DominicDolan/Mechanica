package graphics

import compatibility.Vector
import loader.loadTriangulatedModel
import models.Model
import util.extensions.*

class Polygon(val path: List<Vector>) {
    val centroid: Vector = path.centroid()
    private var modelInstance: Model? = null
    val model: Model
        get() {
            val model: Model = modelInstance ?: loadTriangulatedModel(path)
            this.modelInstance = model
            return model
        }

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

        modelInstance = loadTriangulatedModel(path)

    }
}