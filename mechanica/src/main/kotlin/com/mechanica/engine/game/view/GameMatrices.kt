package com.mechanica.engine.game.view

import com.cave.library.angle.Radian
import com.cave.library.angle.degrees
import com.cave.library.matrix.mat4.Matrix4
import com.mechanica.engine.display.Display
import com.mechanica.engine.display.DrawSurface
import com.mechanica.engine.util.CameraMatrices

internal class GameMatrices(
        private val surface: DrawSurface,
        private val projectionConfiguration: (Matrix4.(View) -> Unit),
        display: Display,
        viewPort: View) : CameraMatrices {
    override val projection: Matrix4 = Matrix4.identity()
    override val worldCamera: Matrix4 = Matrix4.identity()
    override val uiCamera = Matrix4.identity()

    val pvMatrix = Matrix4.identity()
    val pvUiMatrix = Matrix4.identity()

    var pixelScale: Float
    var pixelUIScale: Float

    init {
        projectionConfiguration(projection, viewPort)

        updateView(viewPort)
        setUiView(viewPort.height/display.contentScale.yScale)

        pixelScale = calculatePixelSize(projection, worldCamera, surface.height)
        pixelUIScale = calculatePixelSize(projection, uiCamera, surface.height)
    }

    fun updateView(view: View) {
        val height = view.height
        val x = view.x
        val y = view.y

        val cameraZ = height*projection.scale.y/2f
        worldCamera.translation.set(-x, -y, -cameraZ)

        projectionConfiguration(projection, view)

        pixelScale = calculatePixelSize(projection, worldCamera, surface.height)
    }

    fun setUiView(height: Double) {
        val cameraZ = height*projection.scale.y/(2f)
        uiCamera.translation.set(0.0, 0.0, -cameraZ)
        pixelUIScale = calculatePixelSize(projection, uiCamera, surface.height)
    }

    fun updateMatrices() {
        pvMatrix.set(projection)
        pvMatrix *= worldCamera

        pvUiMatrix.set(projection)
        pvUiMatrix *= uiCamera
    }

    companion object {
        private const val fov = 70.0
        private val fovRadian: Radian = fov.degrees.toRadians()

        fun defaultProjectionMatrix(matrix: Matrix4, view: View) {

            val sm = Matrix4.perspective(fovRadian, view.ratio, 0.0, Double.POSITIVE_INFINITY)
            matrix.set(sm)

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