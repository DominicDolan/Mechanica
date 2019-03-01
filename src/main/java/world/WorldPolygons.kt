package world

import compatibility.Polygon
import java.util.*

/**
 * Created by domin on 01/11/2017.
 */
class WorldPolygons : Iterable<compatibility.Polygon>{
    private val polygons: ArrayList<compatibility.Polygon> = ArrayList()

    fun add(polygon: Polygon){
        polygons.add(polygon)
    }

    override fun iterator() = polygons.iterator()

    operator fun get(i: Int) = polygons[i]

    override fun toString(): String {
        var str = ""
        polygons.forEach { str += "\n" + it }
        return str
    }

}