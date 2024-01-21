package com.mechanica.engine.drawer.subclasses.transformation

import com.cave.library.angle.Angle
import com.cave.library.angle.radians
import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.drawer.Drawer

interface TransformationDrawer : Drawer {
    fun translate(x: Number, y: Number): TransformationDrawer
    
    fun translate(translation: InlineVector) = translate(translation.x, translation.y)
    fun translate(translation: Vector2) = translate(translation.x, translation.y)
    
    fun scale(x: Number, y: Number): TransformationDrawer
    
    fun scale(scale: InlineVector) = scale(scale.x, scale.y)
    fun scale(scale: Vector2) = scale(scale.x, scale.y)
    fun scale(scale: Double) = scale(scale, scale)

    fun skew(horizontal: Angle = 0.radians, vertical: Angle = 0.radians): TransformationDrawer

    fun matrix(matrix: Matrix4): TransformationDrawer

    operator fun invoke(x: Number = 0.0, y: Number = 0.0, scaleX: Number = 1.0, scaleY: Number = 1.0): TransformationDrawer

    operator fun invoke(matrix: Matrix4) = matrix(matrix)
}