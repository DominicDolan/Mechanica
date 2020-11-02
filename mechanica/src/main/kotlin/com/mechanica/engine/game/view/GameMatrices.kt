package com.mechanica.engine.game.view

import com.mechanica.engine.display.Monitor
import com.mechanica.engine.display.Window
import com.mechanica.engine.matrix.Matrices
import com.mechanica.engine.matrix.calculatePixelSize
import com.mechanica.engine.matrix.yScale
import org.joml.Matrix4f

internal class GameMatrices(
        private val window: Window,
        private val projectionConfiguration: (Matrix4f.(View) -> Unit),
        viewPort: View) : Matrices {
    override val projection: Matrix4f = Matrix4f().identity()
    override val worldCamera: Matrix4f = Matrix4f().identity()
    override val uiCamera = Matrix4f()

    val pvMatrix = Matrix4f()
    val pvUiMatrix = Matrix4f()

    var pixelScale: Float
    var pixelUIScale: Float

    init {
        projectionConfiguration(projection, viewPort)

        updateView(viewPort)
        setUiView(viewPort.height/Monitor.getPrimaryMonitor().contentScale.yScale)

        pixelScale = calculatePixelSize(projection, worldCamera, window.height)
        pixelUIScale = calculatePixelSize(projection, uiCamera, window.height)
    }

    fun updateView(view: View) {
        val height = view.height
        val x = view.x
        val y = view.y

        val cameraZ = height*projection.yScale/2f
        worldCamera.setTranslation(-x.toFloat(), -y.toFloat(), -cameraZ.toFloat())

        projectionConfiguration(projection, view)

        pixelScale = calculatePixelSize(projection, worldCamera, window.height)
    }

    fun setUiView(height: Double) {
        val cameraZ = height*projection.yScale/(2f)
        uiCamera.setTranslation(0f, 0f, -cameraZ.toFloat())
        pixelUIScale = calculatePixelSize(projection, uiCamera, window.height)
    }

    fun updateMatrices() {
        pvMatrix.set(projection)
        pvMatrix.mul(worldCamera)

        pvUiMatrix.set(projection)
        pvUiMatrix.mul(uiCamera)
    }

    companion object {
        private const val fov = 70f
        private val fovRadian: Float
            get() = Math.toRadians(fov.toDouble()).toFloat()

        fun defaultProjectionMatrix(matrix: Matrix4f, view: View) {
            val aspectRatio = view.ratio.toFloat()

            matrix.setPerspective(fovRadian, aspectRatio, 0f, 1f, false)

            matrix._m22(-1f)
            matrix._m32(0f)
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