package matrices

import org.joml.Matrix4f
import org.joml.Vector3f

/**
 * Created by domin on 23 Mar 2017.
 */

class TransformationMatrix {
    private val matrix: Matrix4f = Matrix4f().identity()
    private val translation: Vector3f = Vector3f()
    private val pivot: Vector3f = Vector3f()
    private val unpivot: Vector3f = Vector3f()
    private val scale: Vector3f = Vector3f(1f, 1f, 1f)
    private val xAxis = Vector3f(1f, 0f, 0f)
    private val yAxis = Vector3f(0f, 1f, 0f)
    private val zAxis = Vector3f(0f, 0f, 1f)
    private var rx = 0f
    private var ry = 0f
    private var rz = 0f
    val scaleX = 1f
    val scaleY = 1f
    @Suppress("unused")
    val scaleZ = 1f // ScaleZ will be unused most of the time

    private var scheduleRotation = true

    fun setTranslate(x: Float, y: Float, z: Float) {
        translation.set(x, y, z)
    }

    fun setRotate(x: Float, y: Float, z: Float) {
        rx = x
        ry = y
        rz = z
        scheduleRotation = true
    }

    fun setScale(x: Float, y: Float, z: Float) {
        scale.set(x, y, z)
    }

    fun setPivot(pivotX: Float, pivotY: Float) {
        pivot.set(pivotX, pivotY, 0f)
        unpivot.set((-pivotX), (-pivotY), 0f)
        scheduleRotation = true
    }

    fun get(matrix: Matrix4f = this.matrix): Matrix4f {
        matrix.identity()
        matrix.translate(translation)
        if (scheduleRotation) {
            if (pivot.x != 0f || pivot.y != 0f)
                matrix.translate(pivot)

            matrix.rotate(rx, xAxis)
            matrix.rotate(ry, yAxis)
            matrix.rotate(rz, zAxis)

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
        setRotate(0f, 0f, 0f)
        setTranslate(0f, 0f, 0f)
        setScale(1f, 1f, 1f)
        setPivot(0f, 0f)
    }


}
