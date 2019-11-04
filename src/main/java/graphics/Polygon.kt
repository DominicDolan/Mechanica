package graphics

import models.Model
import util.units.Vector

interface Polygon {
    val path: List<Vector>
    val model: Model
}