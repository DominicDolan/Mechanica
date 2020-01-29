package graphics

import models.Model
import util.units.Vector

interface Polygon : Iterable<Vector> {
    val path: List<Vector>
    val model: Model

    override fun iterator(): Iterator<Vector> {
        return path.iterator()
    }
}