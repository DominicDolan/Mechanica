package drawer.subclasses.rotation

import drawer.Drawer
import drawer.DrawData
import util.units.Degree
import util.units.LightweightVector
import util.units.Radian
import util.units.Vector

internal class RotatedDrawerImpl(drawer: Drawer, private val data: DrawData): RotatedDrawer, Drawer by drawer {

    override fun invoke(angle: Degree) = rotate(angle.toRadians().asDouble())

    override fun invoke(angle: Radian) = rotate(angle.asDouble())

    override fun about(pivotX: Number, pivotY: Number): Drawer {
        data.modelOrigin.set(pivotX.toDouble(), pivotY.toDouble())
        return this
    }

    override fun about(point: LightweightVector) = about(point.x, point.y)
    override fun about(point: Vector) = about(point.x, point.y)

    private fun rotate(radians: Double): RotatedDrawer {
        data.setRotate(radians.toFloat())
        return this
    }

}