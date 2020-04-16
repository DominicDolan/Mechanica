package geometry.lines

import geometry.triangulation.TriangulatorList
import util.units.LightweightVector

class PolygonLine(private val list: ArrayList<TriangulatorList.Node>, val listIndex: Int) :  LineSegment() {

    override val p1: TriangulatorList.Node
        get() = list[listIndex]
    override val p2: TriangulatorList.Node
        get() = list[listIndex].next

    fun hasChanged(hasChange: Boolean) {
        this.hasChanged = hasChange
    }

    fun setP1(vec: LightweightVector, previous: PolygonLine? = null) {
        p1.x = vec.x
        p1.y = vec.y
        hasChanged = true
        previous?.hasChanged(true)
    }

    fun setP2(vec: LightweightVector, next: PolygonLine? = null) {
        p2.x = vec.x
        p2.y = vec.y
        hasChanged = true
        next?.hasChanged(true)
    }


}