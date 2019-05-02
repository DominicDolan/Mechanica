package svg

import compatibility.Vector
import loader.loadTextFile
import org.jbox2d.common.Vec2
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.lang.NumberFormatException
import java.util.*


fun loadPolygonCoordinatesAdjusted(fileName: String, pathId: String? = null): List<Vector> {
    val doc = loadSVGDocument(fileName)
    val element = if (pathId == null) {
        doc.getElementsByTag("path")[0]
    } else {
        doc.getElementById(pathId)
    }
    val path = loadPolygonCoordinatesAsIs(element)
    path.toOrigin()
    path.scale(1.0/100.0)
    return path
}

fun loadPolygonCoordinatesAsIs(element: Element): List<Vector> {
    val pathStr = element
            .attr("d")
            .toUpperCase()
            .replace("M", "")
            .replace("Z", "")
            .trim()
            .also { println("Remove m and z: $it") }

    val split = if (pathStr.contains("L")) {
        println("Contains L")
        pathStr.split("L", "C")
    } else pathStr.split(Regex("\\s"))
    println("First split: ${pathStr.split(Regex("\\s"))}")
    return split.map { coordsStr ->
        val coords: List<Double> = coordsStr
                .trim()
                .also { println("trim: $it") }
                .replace(" ,", ",")
                .replace(", ", ",")
                .split(",", " ")
                .also { println("Second split: ${it.toString()}") }
                .map { it.toDouble() }
        Vector(coords[0], coords[1])
    }
    .flipVertically()
}

fun loadSVGPolygon(fileName: String, pathId: String? = null): SVGPolygon {
    val doc = loadSVGDocument(fileName)
    val element = if (pathId == null) {
        doc.getElementsByTag("path")[0]
    } else {
        doc.getElementById(pathId)
    }

    return SVGPolygon(element)
}

fun loadSVGDocument(fileName: String): Document {
    val file = loadTextFile(fileName)
    return Jsoup.parse(file)
}

fun getWidthAndHeight(element: Element): Vec2 {
    return element.getVector("width", "height")
}

fun Element.getVector(xAttr: String, yAttr: String): Vec2 {
    val xStr = this.attr(xAttr)
    val yStr = this.attr(yAttr)

    val x = try {
        Regex("\\d+").find(xStr)?.value?.toFloat() ?: 0f
    } catch (e: NumberFormatException) { 0f }
    val y = try {
        Regex("\\d+").find(yStr)?.value?.toFloat() ?: 0f
    } catch (e: NumberFormatException) { 0f }

    return Vec2(-x, -y)
}
