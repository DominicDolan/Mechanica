package com.mechanica.engine.drawer.superclass.text

import com.mechanica.engine.text.Text
import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector

interface TextDrawer {
    fun text(text: String)

    fun text(text: String, size: Number = 1.0, x: Number = 0.0, y: Number = 0.0)
    fun text(text: String, size: Number, position: Vector) = text(text, size, position.x, position.y)
    fun text(text: String, size: Number, position: LightweightVector) = text(text, size, position.x, position.y)

    fun text(text: Text)

    fun text(text: Text, size: Number = 1.0, x: Number = 0.0, y: Number = 0.0)
    fun text(text: Text, size: Number, position: Vector) = text(text, size, position.x, position.y)
    fun text(text: Text, size: Number, position: LightweightVector) = text(text, size, position.x, position.y)
}