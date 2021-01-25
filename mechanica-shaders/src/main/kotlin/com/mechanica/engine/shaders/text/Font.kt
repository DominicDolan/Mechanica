package com.mechanica.engine.shaders.text

import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.text.CharacterCursor
import com.mechanica.engine.unit.vector.DynamicVector

abstract class Font {
    abstract val atlas: Image

    abstract val ascent: Float
    abstract val descent: Float
    abstract val lineGap: Float
    val lineHeight: Float
        get() = ascent - descent+lineGap

    abstract fun addCharacterDataToArrays(cursor: CharacterCursor, positions: Array<DynamicVector>, texCoords: Array<DynamicVector>)

    companion object
}