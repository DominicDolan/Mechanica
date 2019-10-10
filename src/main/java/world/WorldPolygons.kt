package world

import compatibility.PolygonConverter
import java.util.*

/**
 * Created by domin on 01/11/2017.
 */
class WorldPolygons : Iterable<compatibility.PolygonConverter>{
    private val polygonConverters: ArrayList<compatibility.PolygonConverter> = ArrayList()

    fun add(polygonConverter: PolygonConverter){
        polygonConverters.add(polygonConverter)
    }

    override fun iterator() = polygonConverters.iterator()

    operator fun get(i: Int) = polygonConverters[i]

    override fun toString(): String {
        var str = ""
        polygonConverters.forEach { str += "\n" + it }
        return str
    }

}