package geometry.triangulation

import geometry.isInTriangle
import geometry.rectangleArea
import util.extensions.vec
import util.units.LightweightVector
import util.units.Vector


val TriangulatorList.zeroNode
    get() = Node(vec(0, 0))

fun calculateLineArea(p1: LightweightVector, p2: LightweightVector): Double {
    return (p2.x - p1.x)*(p2.y + p1.y)
}

fun TriangulatorList.Node.isConcave() = isConcave(prev, this, next, isCCW)

fun isConcave(prev: Vector, current: Vector, next: Vector, ccw: Boolean): Boolean {
    val area = rectangleArea(next, prev, current)
    val isLeft = area > 0.0
    return (isLeft && !ccw) || (!isLeft && ccw)
}

fun TriangulatorList.Node.isEar(concaveVertices: Iterable<TriangulatorList.Node>): Boolean {

    val p2 = this.next
    val p3 = this.prev
    for (n in concaveVertices) {
        if (n.isInTriangle(this, p2, p3)) return false
    }
    return (!this.isConcave)
}


