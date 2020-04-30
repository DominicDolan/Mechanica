package com.mechanica.engine.drawer.superclass.text

import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector

interface TextDrawer {
    fun text(text: String)

    fun text(text: String, size: Number, x: Number, y: Number)
    fun text(text: String, size: Number, position: Vector) = text(text, size, position.x, position.y)
    fun text(text: String, size: Number, position: LightweightVector) = text(text, size, position.x, position.y)
}