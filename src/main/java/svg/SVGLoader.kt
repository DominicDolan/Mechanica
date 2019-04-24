package svg

import compatibility.Vector
import loader.loadTextFile
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


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
    return element
            .attr("d")
            .replace("M", "")
            .replace("z", "")
            .split("L", "C")
            .map { coordsStr ->
                val coords: List<Double> = coordsStr
                        .split(",")
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
