package compatibility

import org.jbox2d.common.Vec2
import svg.SVGPolygon

fun SVGPolygon.toVecArray(): Array<Vec2> {
    val v = this.path
    return Array(v.size) {
        Vec2(v[it].toVec2())
    }
}
