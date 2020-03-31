package game.view

import game.configuration.GameSetup
import input.Cursor
import input.Mouse
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.tan

internal class GameMatrices(data: GameSetup, viewPort: View) : Matrices {
    override val projection: Matrix4f// = Matrix4f()
    override val view = Matrix4f().identity()
    override val uiView = Matrix4f()

    val pvMatrix = Matrix4f()
    val pvUiMatrix = Matrix4f()

    init {
        updateView(data.viewX, data.viewY, data.viewHeight)
        setUiView(view)

        projection = Matrix4f().identity()
        data.projectionMatrixConfiguration(projection, viewPort)

    }

    fun updateView(viewData: GameView) {
        updateView(viewData.x, viewData.y, viewData.height)
        Cursor.worldX = Cursor.viewX + viewData.x
        Cursor.worldY = Cursor.viewY + viewData.y
        Mouse.worldX = Mouse.viewX + viewData.x
        Mouse.worldY = Mouse.viewY + viewData.y
    }

    private fun updateView(x: Double, y: Double, height: Double) {
        val fov = Math.toRadians(fov.toDouble())

/*      δ = 2*atan(d/2D)
        δ is angular diameter or fov, 70 degrees.
        d is actual size, 10
        D is distance away. ?
        D = d/(2*tan(δ/2))
*/      val cameraZ = (height) / (2 * tan(fov / 2))
        view.setTranslation(-x.toFloat(), -y.toFloat(), -cameraZ.toFloat())
//        projection.scheduleCreation = true
    }

    private fun setUiView(view: Matrix4f) {
        uiView.set(view)

        val translation = Vector3f()
        view.getTranslation(translation)
        uiView.setTranslation(0f, 0f, translation.z)
    }

    fun updateMatrices() {
        pvMatrix.set(projection)
        pvMatrix.mul(view)

        pvUiMatrix.set(projection)
        pvUiMatrix.mul(uiView)
    }

    companion object {
        const val farPlane: Float = 1000f
        const val nearPlane: Float = 0.1f
        const val fov = 70f

        fun defaultProjectionMatrix(matrix: Matrix4f, view: View) {
            val aspectRatio = view.ratio.toFloat()
            val fov = Math.toRadians(fov.toDouble()).toFloat()
            matrix.setPerspective(fov, aspectRatio, nearPlane, farPlane, false)
//
//            matrix.zero()
//            val yScale = (1f / tan(Math.toRadians((this.fov / 2f).toDouble())) * aspectRatio).toFloat()
//            val xScale = (yScale / aspectRatio).toFloat()
//            val frustumLength = this.farPlane - nearPlane
//            //      0   1   2   3
//            //  0 [ 0   1   2   3 ]
//            //  1 [ 4   5   6   7 ]
//            //  2 [ 8   9   10  11]
//            //  3 [ 12  13  14  15]
//            //
//            matrix._m00( xScale )
//            matrix._m11( yScale )
//            matrix._m22( -((this.farPlane + nearPlane) / frustumLength) )
//            matrix._m23( -1f )
//            matrix._m32( -(2f * nearPlane * this.farPlane / frustumLength) )
//            matrix._m33( 0f )
//
//            //            matrix[0] = x_scale;
//            //            matrix[5] = y_scale;
//            //            matrix[10] = -((farPlane + nearPlane)/frustum_length);
//            //            matrix[11] = -1;
//            //            matrix[14] = -((2*nearPlane*farPlane)/frustum_length);
//            //            matrix[15] = 0;
        }
    }
}