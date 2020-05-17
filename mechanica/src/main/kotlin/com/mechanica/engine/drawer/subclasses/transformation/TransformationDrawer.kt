package com.mechanica.engine.drawer.subclasses.transformation

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector

interface TransformationDrawer : Drawer {
    fun translate(x: Number, y: Number): TransformationDrawer
    
    fun translate(translation: LightweightVector) = translate(translation.x, translation.y)
    fun translate(translation: Vector) = translate(translation.x, translation.y)
    
    fun scale(x: Number, y: Number): TransformationDrawer
    
    fun scale(scale: LightweightVector) = scale(scale.x, scale.y)
    fun scale(scale: Vector) = scale(scale.x, scale.y)
    fun scale(scale: Double) = scale(scale, scale)
}