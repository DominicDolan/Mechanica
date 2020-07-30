package com.mechanica.engine.drawer.subclasses.rotation

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.unit.angle.Degree
import com.mechanica.engine.unit.angle.Radian
import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector

internal class RotatedDrawerImpl(drawer: Drawer, private val data: DrawData): RotatedDrawer, Drawer by drawer {

    override fun invoke(angle: Degree) = rotate(angle.toRadians().toDouble())

    override fun invoke(angle: Radian) = rotate(angle.toDouble())

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