package compatibility

import org.jbox2d.common.Vec2
import svg.SVGPolygon

private val degRad = Math.PI/180.0

fun SVGPolygon.toVecArray(): Array<Vec2> {
    val v = this.path
    return Array(v.size) {
        Vec2(v[it].toVec2())
    }
}

fun Number.toRadians(): Double {
    return this.toDouble()*degRad
}

fun Number.toDegrees(): Double {
    return this.toDouble()/degRad
}