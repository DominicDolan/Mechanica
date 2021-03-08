package com.mechanica.engine.drawer.superclass.text

import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.shaders.text.Text

interface TextDrawer {
    fun text(text: String)

    fun text(text: String, size: Number = 1.0, x: Number = 0.0, y: Number = 0.0)
    fun text(text: String, size: Number, position: Vector2) = text(text, size, position.x, position.y)
    fun text(text: String, size: Number, position: InlineVector) = text(text, size, position.x, position.y)

    fun text(text: Text)

    fun text(text: Text, size: Number = 1.0, x: Number = 0.0, y: Number = 0.0)
    fun text(text: Text, size: Number, position: Vector2) = text(text, size, position.x, position.y)
    fun text(text: Text, size: Number, position: InlineVector) = text(text, size, position.x, position.y)
}