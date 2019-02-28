package matrices

import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * Created by domin on 23 Mar 2017.
 */

class ViewMatrix {
    private val matrix = Matrix4f()
    private var matrixWithProjection = Matrix4f()
    private val translation: Vector3f = Vector3f()
    private val xAxis = Vector3f(1f, 0f, 0f)
    private val yAxis = Vector3f(0f, 1f, 0f)
    private val zAxis = Vector3f(0f, 0f, 1f)
    private var rx = 0f
    private var ry = 0f
    private var rz = 0f

    private var scheduleCreation = true
    private var scheduleProjectionAndView = true

    init {
        matrix.identity()
        matrixWithProjection.identity()
        scheduleCreation = true
    }

    fun setTranslate(x: Double, y: Double, z: Double) {
        translation.set((-x).toFloat(), (-y).toFloat(), (-z).toFloat())
        scheduleCreation = true
    }

    fun setRotate(x: Double, y: Double, z: Double) {
        rx = x.toFloat()
        ry = y.toFloat()
        rz = z.toFloat()
        scheduleCreation = true
    }

    fun create(): Matrix4f {
        if (scheduleCreation) {
            matrix.identity()
            matrix.translate(translation)

            matrix.rotate(Math.toRadians(rx.toDouble()).toFloat(), xAxis)
            matrix.rotate(Math.toRadians(ry.toDouble()).toFloat(), yAxis)
            matrix.rotate(Math.toRadians(rz.toDouble()).toFloat(), zAxis)

            scheduleProjectionAndView = true
        }
        scheduleCreation = false
        return matrix
    }

    fun createWithProjection(projectionMatrix: ProjectionMatrix): Matrix4f {
        if (scheduleCreation || projectionMatrix.scheduleCreation || scheduleProjectionAndView) {
            matrix.identity()
            matrix.translate(translation)

            matrix.rotate(Math.toRadians(rx.toDouble()).toFloat(), xAxis)
            matrix.rotate(Math.toRadians(ry.toDouble()).toFloat(), yAxis)
            matrix.rotate(Math.toRadians(rz.toDouble()).toFloat(), zAxis)

            matrixWithProjection = projectionMatrix.create().mul(matrix)
        }
        scheduleProjectionAndView = false
        scheduleCreation = false
        return matrixWithProjection
    }
}
