package svg

import compatibility.Vector
import java.lang.Long.parseLong
import java.lang.Long.toHexString
import kotlin.math.roundToLong

fun parseColor(hex: String, opacity: String): Long {
    val opacityHex = toHexString(((opacity.toFloat() * 255.0f).roundToLong()))
    return parseLong(hex.replace("#", "") + opacityHex, 16)
}


fun List<Vector>.normalizeX(width: Double): List<Vector> {
    val currentHeight = this.xRange()
    val factor = width/currentHeight
    this.scale(factor)
    return this
}


fun List<Vector>.normalizeY(height: Double): List<Vector> {
    val currentHeight = this.yRange()
    val factor = height/currentHeight
    this.scale(factor)
    return this
}

fun List<Vector>.flipVertically(): List<Vector> {
    this.forEach {
        it.y = -it.y
    }
    return this
}

fun List<Vector>.toOrigin(): List<Vector> {
    val minX = (this.minBy { it.x } ?: Vector(0.0, 0.0)).x
    val minY = (this.minBy { it.y } ?: Vector(0.0, 0.0)).y
    this.forEach {
        it.x = it.x - minX
        it.y = it.y - minY
    }
    return this
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

fun List<Vector>.scale(factor: Double): List<Vector> {
    this.forEach {
        it.x = it.x*factor
        it.y = it.y*factor
    }
    return this
}


fun List<Vector>.scaleX(factor: Double): List<Vector> {
    this.forEach {
        it.x = it.x*factor
    }
    return this
}


fun List<Vector>.scaleY(factor: Double): List<Vector> {
    this.forEach {
        it.y = it.y*factor
    }
    return this
}