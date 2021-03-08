package com.mechanica.engine.shaders.text

import com.cave.library.vector.vec2.VariableVector2
import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.text.CharacterCursor

abstract class Font {
    abstract val atlas: Image

    abstract val ascent: Float
    abstract val descent: Float
    abstract val lineGap: Float
    val lineHeight: Float
        get() = ascent - descent+lineGap

    abstract fun addCharacterDataToArrays(cursor: CharacterCursor, positions: Array<VariableVector2>, texCoords: Array<VariableVector2>)

    companion object
}