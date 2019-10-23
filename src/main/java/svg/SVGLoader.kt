package svg

import compatibility.VectorConverter
import loader.loadTextFile
import org.jbox2d.common.Vec2
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import util.extensions.flipVertically
import util.extensions.scale
import util.extensions.toOrigin
import java.lang.NumberFormatException
import kotlin.collections.ArrayList


fun loadPolygonCoordinatesAdjusted(fileName: String, pathId: String? = null): List<VectorConverter> {
    val doc = loadSVGDocument(fileName)
    val element = if (pathId == null) {
        doc.getElementsByTag("path")[0]
    } else {
        doc.getElementById(pathId)
    }
    val path = loadPolygonCoordinates(element)
    path.toOrigin()
    path.scale(1.0/100.0)
    return path
}

fun loadPolygonCoordinates(element: Element): List<VectorConverter> {
    val sequenceList = element
            .attr("d")
            .replace(Regex("\\s\\s+"), " ")
            .split(Regex("(?=[lLmMzZhHvV])"))
            .filter { it.isNotBlank() }

    val coordinateList = ArrayList<VectorConverter>()
    for (sequence in sequenceList) {
        when (sequence.first()) {
            'M', 'L' -> {
                val absoluteValues = parseCoordinateSequence(sequence.substring(1))
                coordinateList.addAll(absoluteValues)
            }
            'm', 'l' -> {
                val relativeValues = parseCoordinateSequence(sequence.substring(1))
                val initial = if (coordinateList.isNotEmpty()) coordinateList.last() else VectorConverter(0.0, 0.0)
                val absoluteValues = relativeValues.relativeToAbsolute(initial)
                coordinateList.addAll(absoluteValues)
            }
            'z', 'Z' -> {
                coordinateList.add(VectorConverter(coordinateList.first()))
            }
            'H' -> {

            }
            'h' -> {

            }
            'V' -> {

            }
            'v' -> {

            }
        }
    }
    return coordinateList.flipVertically()
}

private fun List<VectorConverter>.relativeToAbsolute(initial: VectorConverter): List<VectorConverter> {
    val absoluteList = ArrayList<VectorConverter>()
    var previous = initial
    for (vector in this) {
        val new = VectorConverter(previous.x + vector.x, previous.y + vector.y)
        absoluteList.add(new)
        previous = new
    }
    return absoluteList
}

fun parseCoordinateSequence(text: String): List<VectorConverter> {
    val individualText = text.trim().split(" ")
    return individualText.map {
        val xy = it.split(",")
        VectorConverter(xy[0].toDouble(), xy[1].toDouble())
    }
}

fun parseSingleValueSequence(text: String): List<Double> {
    val individualText = text.trim().split(" ")
    return individualText.map { it.toDouble() }
}


// This needs to be updated according to this:
// https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/d
fun loadPolygonCoordinatesOld(element: Element): List<VectorConverter> {
    var count = 0
    var mode = 0
    var closed = false
    val pathStr = element
            .attr("d")
            .also { println(it); count = it.length; println("Count before M: $count") }
            .replace("M", "")
            .also { mode = if (count == it.length) 1 else 2; println("Count after M: $count, mode: $mode") }
            .replace("m", "")
            .replace("l", "")
            .replace("  ", " ")
            .also { count = it.length }
            .replace("z", "").replace("Z", "")
            .also { closed = count != it.length }
            .trim()

    val lSplit = pathStr.split("L", "C")
    val listOfArrayOfVerts = lSplit.map {lString ->
        val points = lString.trim().split(",", " ").map { it.trim() }
        val size = points.size/2
        val xValues = Array(size) { points[it*2] }
        val yValues = Array(size) { points[it*2+1] }
        val vertices = Array(size) { VectorConverter(xValues[it].toDouble(), yValues[it].toDouble())}
        vertices
    }
    val returnList = ArrayList<VectorConverter>()
    for (array in listOfArrayOfVerts) {
        when (mode) {
            1 -> {
                val vTotal = VectorConverter(0.0, 0.0)
                array.forEach { v ->
                        vTotal.x += v.x
                        vTotal.y += v.y
                        returnList.add(VectorConverter(vTotal.x, vTotal.y))
                }
            }
            2 -> {
                array.forEach { returnList.add(it) }
            }
        }
    }
    if (closed) {
        val first = returnList[0]
        val last = returnList.last()
        if (first != last) {
            returnList.add(VectorConverter(returnList[0].x, returnList[0].y))
        }
    }

    return returnList.flipVertically()
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
    return Vec2(x, -y)
}
