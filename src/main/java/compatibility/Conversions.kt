package compatibility

import org.jbox2d.common.Vec2
import svg.SVGPolygon
import util.extensions.toVec2
import util.extensions.vec
import util.units.Vector

fun SVGPolygon.toVec2Array(): Array<Vec2> {
    val v = this.path
    return Array(v.size) {
        Vec2(v[it].toVec2())
    }
}

fun SVGPolygon.toLightWeightVectorArray(): Array<Vector> {
    val v = this.path
    return Array(v.size) {
        vec(v[it].x, v[it].y)
    }
}
