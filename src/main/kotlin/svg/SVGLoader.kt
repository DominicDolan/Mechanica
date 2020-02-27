@file:Suppress("unused") // There will be many functions here that go unused most of the time
package svg

import display.Game
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import resources.Resource
import util.extensions.flipVertically
import util.extensions.scale
import util.extensions.toOrigin
import util.extensions.vec
import util.units.MutableVector
import util.units.Vector
import java.lang.NumberFormatException
import kotlin.collections.ArrayList


//fun loadPolygonCoordinatesAdjusted(fileName: String, pathId: String? = null): List<Vector> {
//    val doc = loadSVGDocument(fileName)
//    val element = if (pathId == null) {
//        doc.getElementsByTag("path")[0]
//    } else {
//        doc.getElementById(pathId)
//    }
//    val path = loadPolygonCoordinates(element)
//    path.toOrigin()
//    path.scale(1.0/100.0)
//    return path
//}

fun loadPolygonCoordinates(element: Element): List<Vector> {
    val sequenceList = element
            .attr("d")
            .replace(Regex("\\s\\s+"), " ")
            .split(Regex("(?=[a-zA-Z])"))
            .filter { it.isNotBlank() }

    fun ArrayList<Vector>.addFromString(sequence: String, otherValue: ((Double) -> Vector) = {throw IllegalArgumentException("Error reading svg path: $sequence")}) {
        val initialChar = sequence[0]
        val isAbsolute = initialChar.isLetter() && initialChar.isUpperCase()

        val substring = sequence.substring(1)
        val values = parseCoordinateSequence(substring, otherValue)

        if (isAbsolute) {
            this.addAll(values)
        } else {
            val firstCoordinate = if (this.isNotEmpty()) this.last() else vec(0.0, 0.0)
            val absoluteValues = values.relativeToAbsolute(firstCoordinate)
            this.addAll(absoluteValues)
        }
    }

    val coordinateList = ArrayList<Vector>()
    for (sequence in sequenceList) {
        try {
            when (sequence.first()) {
                'M', 'm', 'L', 'l' -> {
                    coordinateList.addFromString(sequence)
                }
                'z', 'Z' -> {
                    coordinateList.add(coordinateList.first())
                }
                'H', 'h' -> {
                    if (coordinateList.isNotEmpty() && sequence.first() != 'h') {
                        coordinateList.addFromString(sequence) { vec(it, coordinateList.last().y) }
                    } else {
                        coordinateList.addFromString(sequence) { vec(it, 0.0) }
                    }
                }
                'V', 'v' -> {
                    if (coordinateList.isNotEmpty() && sequence.first() != 'v') {
                        coordinateList.addFromString(sequence) { vec(coordinateList.last().x, it) }
                    } else {
                        coordinateList.addFromString(sequence) { vec(0.0, it) }
                    }
                }
                'C', 'c', 'S', 's', 'Q', 'q', 'T', 't', 'A', 'a' -> throw IllegalArgumentException("Reading an SVG does not support curves. Path: $sequence")
                else -> throw IllegalArgumentException("Unexpected character while reading SVG file. Path: $sequence")
            }
        } catch (e: IllegalArgumentException) {
            if (Game.debug) {
                throw e
            } else {
                System.err.println(e.localizedMessage)
            }
        }
    }
    return coordinateList.flipVertically()
}

private fun List<Vector>.relativeToAbsolute(initial: Vector): List<Vector> {
    val absoluteList = ArrayList<Vector>()
    var previous = initial
    for (vector in this) {
        val new = vec(previous.x + vector.x, previous.y + vector.y)
        absoluteList.add(new)
        previous = new
    }
    return absoluteList
}

fun parseCoordinateSequence(text: String, otherValue: ((Double) -> Vector)): List<Vector> {
    val individualText = text.trim().split(" ")
    return individualText.map {
        if (it.contains(",")) {
            val xy = it.split(",")
            vec(xy[0].toDouble(), xy[1].toDouble())
        } else {
            otherValue(it.trim().toDouble())
        }
    }
}

fun parseSingleValueCoordinate(text: String): List<Vector> {
    val individualText = text.trim().split(" ")
    return individualText.map {
        val xy = it.split(",")
        vec(xy[0].toDouble(), xy[1].toDouble())
    }
}

fun parseSingleValueSequence(text: String): List<Double> {
    val individualText = text.trim().split(" ")
    return individualText.map { it.toDouble() }
}


// This needs to be updated according to this:
// https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/d
fun loadPolygonCoordinatesOld(element: Element): List<Vector> {
    var count: Int
    var mode: Int
    var closed: Boolean
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
        val vertices = Array(size) { vec(xValues[it].toDouble(), yValues[it].toDouble())}
        vertices
    }
    val returnList = ArrayList<Vector>()
    for (array in listOfArrayOfVerts) {
        when (mode) {
            1 -> {
                val vTotal = MutableVector(0.0, 0.0)
                array.forEach { v ->
                        vTotal.x += v.x
                        vTotal.y += v.y
                        returnList.add(vec(vTotal.x, vTotal.y))
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
            returnList.add(vec(returnList[0].x, returnList[0].y))
        }
    }

    return returnList.flipVertically()
}

//fun loadSVGPolygon(fileName: String, pathId: String? = null): SVGPolygon {
//    val doc = loadSVGDocument(fileName)
//    val element = if (pathId == null) {
//        doc.getElementsByTag("path")[0]
//    } else {
//        doc.getElementById(pathId)
//    }
//
//    return SVGPolygon(element)
//}

fun loadSVGDocument(resource: Resource): Document {
    val file = resource.contents
    return Jsoup.parse(file)
}

fun getWidthAndHeight(element: Element): Vector {
    return element.getVector("width", "height")
}

private val regexMatchNumber = Regex("^-?[0-9]\\d*(\\.\\d+)?$")
fun Element.getVector(xAttr: String, yAttr: String): Vector {
    val xStr = this.attr(xAttr)
    val yStr = this.attr(yAttr)

    val x = try {
        regexMatchNumber.find(xStr)?.value?.toFloat() ?: 0f
    } catch (e: NumberFormatException) { 0f }
    val y = try {
        regexMatchNumber.find(yStr)?.value?.toFloat() ?: 0f
    } catch (e: NumberFormatException) { 0f }
    return vec(x, -y)
}
