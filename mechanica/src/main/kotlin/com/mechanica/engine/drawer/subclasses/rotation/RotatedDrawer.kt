package com.mechanica.engine.drawer.subclasses.rotation

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.unit.angle.Degree
import com.mechanica.engine.unit.angle.Radian
import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.Vector

interface RotatedDrawer : Drawer {

    operator fun invoke(angle: Degree): RotatedDrawer
    operator fun invoke(angle: Radian): RotatedDrawer

    fun about(pivotX: Number, pivotY: Number): Drawer
    fun about(point: InlineVector): Drawer
    fun about(point: Vector): Drawer
}