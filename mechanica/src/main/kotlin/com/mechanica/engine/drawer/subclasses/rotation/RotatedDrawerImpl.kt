package com.mechanica.engine.drawer.subclasses.rotation

import com.cave.library.angle.Degree
import com.cave.library.angle.Radian
import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.state.DrawState

internal class RotatedDrawerImpl(drawer: Drawer, private val state: DrawState): RotatedDrawer, Drawer by drawer {

    override fun invoke(angle: Degree) = rotate(angle.toRadians())

    override fun invoke(angle: Radian) = rotate(angle)

    override fun about(pivotX: Number, pivotY: Number): Drawer {
        state.origin.relative.set(pivotX.toDouble(), pivotY.toDouble())
        return this
    }

    override fun about(point: InlineVector) = about(point.x, point.y)
    override fun about(point: Vector2) = about(point.x, point.y)

    private fun rotate(radians: Radian): RotatedDrawer {
        state.setRotate(radians)
        return this
    }

}