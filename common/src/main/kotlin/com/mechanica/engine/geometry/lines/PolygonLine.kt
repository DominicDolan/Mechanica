package com.mechanica.engine.geometry.lines

import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.geometry.triangulation.Triangulator

class PolygonLine(private val list: ArrayList<Triangulator.Node>, val listIndex: Int) :  LineSegment() {

    override val p1: Triangulator.Node
        get() = list[listIndex]
    override val p2: Triangulator.Node
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