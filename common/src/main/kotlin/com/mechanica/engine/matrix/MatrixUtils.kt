package com.mechanica.engine.matrix

import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.min

private val vec3 = Vector3f()

fun calculatePixelSize(projection: Matrix4f, view: Matrix4f, resolutionHeight: Int, zTranslation: Float = 0f): Float {
    val zTranslate = (zTranslation + view.zTranslation)


    /* The following equations help to work out the y height:
           zTranslate = height / (2 * tan(fov / 2))
       For Projection Matrix:
           ScaleY = (1f / tan(fov / 2f)) * aspectRatio  */
    val yHeight = zTranslate/projection.yScale

    val scale = view.minScale
    val fractionOfScreen = scale/yHeight
    val sizeOnScreen = fractionOfScreen* resolutionHeight

    return 2f/sizeOnScreen
}

val Matrix4f.yScale: Float
    get() {
        this.getScale(vec3)
        return vec3.y
    }

private val Matrix4f.minScale: Float
    get() {
        this.getScale(vec3)
        return min(vec3.y, vec3.x)
    }

private val Matrix4f.zTranslation: Float
    get() {
        this.getTranslation(vec3)
        return -vec3.z
    }
