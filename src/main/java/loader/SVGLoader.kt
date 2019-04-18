package loader

import compatibility.Vector
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


fun loadPolygon(fileName: String): List<Vector> {
    val doc = loadSVGDocument(fileName)
    return doc
            .getElementById("svg_4")
            .attr("d")
            .replace("M", "")
            .replace("z", "")
            .split("L")
            .map { coordsStr ->
                val coords: List<Double> = coordsStr
                        .split(",")
                        .map { it.toDouble() }
                Vector(coords[0], coords[1])
            }
}

fun loadSVGDocument(fileName: String): Document {
    val file = loadTextFile(fileName)


    return Jsoup.parse(file)
}