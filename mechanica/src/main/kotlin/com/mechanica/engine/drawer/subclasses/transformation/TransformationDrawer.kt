package com.mechanica.engine.drawer.subclasses.transformation

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.unit.angle.Degree
import com.mechanica.engine.unit.angle.Radian
import com.mechanica.engine.unit.angle.degrees
import com.mechanica.engine.unit.angle.radians
import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.Vector
import org.joml.Matrix4f

interface TransformationDrawer : Drawer {
    fun translate(x: Number, y: Number): TransformationDrawer
    
    fun translate(translation: InlineVector) = translate(translation.x, translation.y)
    fun translate(translation: Vector) = translate(translation.x, translation.y)
    
    fun scale(x: Number, y: Number): TransformationDrawer
    
    fun scale(scale: InlineVector) = scale(scale.x, scale.y)
    fun scale(scale: Vector) = scale(scale.x, scale.y)
    fun scale(scale: Double) = scale(scale, scale)

    fun skew(horizontal: Degree = 0.degrees, vertical: Degree = 0.degrees): TransformationDrawer = skew(horizontal.toRadians(), vertical.toRadians())
    fun skew(horizontal: Radian = 0.radians, vertical: Radian = 0.radians): TransformationDrawer

    fun matrix(matrix: Matrix4f): TransformationDrawer

    operator fun invoke(x: Number = 0.0, y: Number = 0.0, scaleX: Number = 1.0, scaleY: Number = 1.0): TransformationDrawer

    operator fun invoke(matrix: Matrix4f) = matrix(matrix)
}