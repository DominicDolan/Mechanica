package svg

import compatibility.Vector
import loader.loadTextFile
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


fun loadPolygon(fileName: String, pathId: String? = null): List<Vector> {
    val doc = loadSVGDocument(fileName)
    val element = if (pathId == null) {
        doc.getElementsByTag("path")[0]
    } else {
        doc.getElementById(pathId)
    }

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
}

fun loadSVGDocument(fileName: String): Document {
    val file = loadTextFile(fileName)
    return Jsoup.parse(file)
}

fun List<Vector>.normalize(height: Double) {
    this.toOrigin()
    val currentHeight = this.xRange()
    val factor = height/currentHeight
    this.scale(factor)
}

fun List<Vector>.flipVertically() {
    this.forEach {
        it.y = -it.y
    }
}

fun List<Vector>.toOrigin() {
    val minX = (this.minBy { it.x } ?: Vector(0.0, 0.0)).x
    val minY = (this.minBy { it.y } ?: Vector(0.0, 0.0)).y
    this.forEach {
        it.x = it.x - minX
        it.y = it.y - minY
    }
}

fun List<Vector>.xRange(): Double {
    val max = this.maxBy { it.x } ?: Vector(0.0, 0.0)
    val min = this.minBy { it.x } ?: Vector(0.0, 0.0)
    return max.x - min.x
}

fun List<Vector>.yRange(): Double {
    val max = this.maxBy { it.y } ?: Vector(0.0, 0.0)
    val min = this.minBy { it.y } ?: Vector(0.0, 0.0)
    return max.y - min.y
}

fun List<Vector>.scale(factor: Double) {
    this.forEach {
        it.x = it.x*factor
        it.y = it.y*factor
    }
}