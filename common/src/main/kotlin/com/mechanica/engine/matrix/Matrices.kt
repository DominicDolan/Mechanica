package com.mechanica.engine.matrix

import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.min

interface Matrices {
    val projection: Matrix4f
    val view: Matrix4f
    val uiView: Matrix4f

    companion object {

        private val vec3 = Vector3f()
        fun calculatePixelSize(projection: Matrix4f, view: Matrix4f, resolutionHeight: Int, zTranslation: Float = 0f): Float {
            val zTranslate = (zTranslation + getZTranslate(view))


            /* The following equations help to work out the y height:
                   zTranslate = height / (2 * tan(fov / 2))
               For Projection Matrix:
                   ScaleY = (1f / tan(fov / 2f)) * aspectRatio  */
            val yHeight = zTranslate/getYScale(projection)

            val scale = getMinScale(view)
            val fractionOfScreen = scale/yHeight
            val sizeOnScreen = fractionOfScreen* resolutionHeight

            return 2f/sizeOnScreen
        }

        fun getYScale(mat: Matrix4f): Float {
            mat.getScale(vec3)
            return vec3.y
        }

        private fun getMinScale(mat: Matrix4f): Float {
            mat.getScale(vec3)
            return min(vec3.y, vec3.x)
        }

        private fun getZTranslate(mat: Matrix4f): Float {
            mat.getTranslation(vec3)
            return -vec3.z
        }

    }
}