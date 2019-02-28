package matrices

import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * Created by domin on 23 Mar 2017.
 */

class TransformationMatrix {
    private val matrix: Matrix4f
    private val translation: Vector3f
    private val pivot: Vector3f
    private val unpivot: Vector3f
    private val scale: Vector3f
    private val xAxis = Vector3f(1f, 0f, 0f)
    private val yAxis = Vector3f(0f, 1f, 0f)
    private val zAxis = Vector3f(0f, 0f, 1f)
    private var rx = 0f
    private var ry = 0f
    private var rz = 0f
    val scaleX = 1f
    val scaleY = 1f
    val scaleZ = 1f

    private var scheduleRotation = true
    private var setMatrix = false
    private var customMatrix: Matrix4f = Matrix4f()

    init {
        translation = Vector3f()
        pivot = Vector3f()
        unpivot = Vector3f()
        scale = Vector3f(1f, 1f, 1f)
        matrix = Matrix4f()
        matrix.identity()
    }

    fun setTranslate(x: Double, y: Double, z: Double) {
        translation.set(x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun setRotate(x: Double, y: Double, z: Double) {
        rx = x.toFloat()
        ry = y.toFloat()
        rz = z.toFloat()
        scheduleRotation = true
    }

    fun setScale(x: Double, y: Double, z: Double) {
        scale.set(x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun setPivot(pivotX: Double, pivotY: Double) {
        pivot.set(pivotX.toFloat(), pivotY.toFloat(), 0f)
        unpivot.set((-pivotX).toFloat(), (-pivotY).toFloat(), 0f)
        scheduleRotation = true
    }

    fun setMatrix(matrix: Matrix4f) {
        setMatrix = true
        customMatrix = matrix
    }

    fun create(): Matrix4f {
        if (setMatrix) {
            setMatrix = false
            return customMatrix
        }
        matrix.identity()
        matrix.translate(translation)
        if (scheduleRotation) {
            if (pivot.x != 0f || pivot.y != 0f)
                matrix.translate(pivot)

            matrix.rotate(Math.toRadians(rx.toDouble()).toFloat(), xAxis)
            matrix.rotate(Math.toRadians(ry.toDouble()).toFloat(), yAxis)
            matrix.rotate(Math.toRadians(rz.toDouble()).toFloat(), zAxis)

            if (pivot.x != 0f || pivot.y != 0f)
                matrix.translate(unpivot)
        }
        scheduleRotation = false
        matrix.scale(scale)
        pivot.set(0f, 0f, 0f)
        unpivot.set(0f, 0f, 0f)
        rx = 0f
        ry = 0f
        rz = 0f
        return matrix
    }

    fun rewind() {
        matrix.identity()
        setRotate(0.0, 0.0, 0.0)
        setTranslate(0.0, 0.0, 0.0)
        setScale(1.0, 1.0, 1.0)
        setPivot(0.0, 0.0)
    }


}
