package game.view

import debug.ScreenLog
import display.Monitor
import game.Game
import game.configuration.GameSetup
import game.view.Matrices.Companion.calculatePixelSize
import game.view.Matrices.Companion.getYScale
import input.Mouse
import org.joml.Matrix4f
import org.joml.Vector3f
import util.units.DynamicVector
import kotlin.math.tan

internal class GameMatrices(data: GameSetup, viewPort: View) : Matrices {
    override val projection: Matrix4f = Matrix4f().identity()// = Matrix4f()
    override val view: Matrix4f = Matrix4f().identity()
    override val uiView = Matrix4f()

    val pvMatrix = Matrix4f()
    val pvUiMatrix = Matrix4f()

    var pixelScale: Float
    var pixelUIScale: Float

    init {
        data.projectionMatrixConfiguration(projection, viewPort)
        updateView(data.viewX, data.viewY, data.viewHeight)
        setUiView(data.viewHeight)

        pixelScale = calculatePixelSize(projection, view)
        pixelUIScale = calculatePixelSize(projection, uiView)
    }

    fun updateView(viewData: GameView) {
        updateView(viewData.x, viewData.y, viewData.height)

        Mouse.refreshCursor()
    }

    private fun updateView(x: Double, y: Double, height: Double) {
        val cameraZ = height*getYScale(projection)/2f
        view.setTranslation(-x.toFloat(), -y.toFloat(), -cameraZ.toFloat())
        pixelScale = calculatePixelSize(projection, view)
    }

    private fun setUiView(height: Double) {
        val cameraZ = height*getYScale(projection)/(2f*Monitor.getPrimaryMonitor().contentScale.yScale)
        uiView.setTranslation(0f, 0f, -cameraZ.toFloat())
        pixelUIScale = calculatePixelSize(projection, view)
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