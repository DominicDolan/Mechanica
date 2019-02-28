package world

import org.jsoup.nodes.Document

/**
 * Created by domin on 01/11/2017.
 */
class WorldSection(val graphics: WorldGraphics, val polygons: WorldPolygons, private val doc: Document) {

    operator fun get(id: String, innerID: String, attribute: String): String{
        return doc.getElementById(id).getElementById(innerID).attr(attribute)
    }

    operator fun get(id: String, attribute: String): String{
        return doc.getElementById(id).attr(attribute)
    }

    fun getValues(id: String, tag: String, attribute: String): Array<String>{
        val elements = doc.getElementById(id).getElementsByTag(tag)
        return Array(elements.size, {elements[it].attr(attribute)})
    }
}