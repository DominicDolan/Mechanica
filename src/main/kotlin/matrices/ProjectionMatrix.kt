@file:Suppress("unused") // There will be many functions here that go unused most of the time
package matrices

import game.Game
import org.joml.Matrix4f
import kotlin.math.tan

/**
 * Created by domin on 23 Mar 2017.
 */

class ProjectionMatrix {
    private val matrix: Matrix4f = Matrix4f()

    var fov = 70f
        set(fov) {
            field = fov
            scheduleCreation = true
        }
    private var nearPlane = 0.1f
    var farPlane = 1000f
        set(farPlane) {
            field = farPlane
            scheduleCreation = true
        }

    var scheduleCreation = true

    init {
        matrix.identity()
        scheduleCreation = true
    }

    fun setNearPlane(nearPlane: Float) {
        this.nearPlane = nearPlane
        scheduleCreation = true
    }

    fun get(): Matrix4f {
        if (scheduleCreation) {
            val aspectRatio = Game.window.aspectRatio

            val yScale = (1f / tan(Math.toRadians((this.fov / 2f).toDouble())) * aspectRatio).toFloat()
            val xScale = (yScale / aspectRatio).toFloat()
            val frustumLength = this.farPlane - nearPlane
            //      0   1   2   3
            //  0 [ 0   1   2   3 ]
            //  1 [ 4   5   6   7 ]
            //  2 [ 8   9   10  11]
            //  3 [ 12  13  14  15]
            //
            matrix._m00( xScale )
            matrix._m11( yScale )
            matrix._m22( -((this.farPlane + nearPlane) / frustumLength) )
            matrix._m23( -1f )
            matrix._m32( -(2f * nearPlane * this.farPlane / frustumLength) )
            matrix._m33( 0f )

            //            matrix[0] = x_scale;
            //            matrix[5] = y_scale;
            //            matrix[10] = -((farPlane + nearPlane)/frustum_length);
            //            matrix[11] = -1;
            //            matrix[14] = -((2*nearPlane*farPlane)/frustum_length);
            //            matrix[15] = 0;
        }
        scheduleCreation = false
        return matrix
    }
}
