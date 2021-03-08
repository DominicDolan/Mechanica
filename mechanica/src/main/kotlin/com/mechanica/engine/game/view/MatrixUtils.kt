package com.mechanica.engine.game.view

import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.vector.vec3.component1
import com.cave.library.vector.vec3.component2
import kotlin.math.min

fun calculatePixelSize(projection: Matrix4, view: Matrix4, resolutionHeight: Int, zTranslation: Float = 0f): Float {
    val zTranslate = (zTranslation + view.translation.z)


    /* The following equations help to work out the y height:
           zTranslate = height / (2 * tan(fov / 2))
       For Projection Matrix:
           ScaleY = (1f / tan(fov / 2f)) * aspectRatio  */
    val yHeight = zTranslate/projection.scale.y

    val (scaleX, scaleY) = view.scale
    val scale = min(scaleX, scaleY)

    val fractionOfScreen = scale/yHeight
    val sizeOnScreen = fractionOfScreen* resolutionHeight

    return 2f/sizeOnScreen.toFloat()
}

