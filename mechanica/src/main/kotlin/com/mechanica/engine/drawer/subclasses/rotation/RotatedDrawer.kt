package com.mechanica.engine.drawer.subclasses.rotation

import com.cave.library.angle.Degree
import com.cave.library.angle.Radian
import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.drawer.Drawer

interface RotatedDrawer : Drawer {

    operator fun invoke(angle: Degree): RotatedDrawer
    operator fun invoke(angle: Radian): RotatedDrawer

    fun about(pivotX: Number, pivotY: Number): Drawer
    fun about(point: InlineVector): Drawer
    fun about(point: Vector2): Drawer
}