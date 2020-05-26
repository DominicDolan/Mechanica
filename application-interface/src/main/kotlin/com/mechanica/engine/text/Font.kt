package com.mechanica.engine.text

import com.mechanica.engine.models.Image
import com.mechanica.engine.context.loader.GLLoader
import com.mechanica.engine.resources.Resource
import com.mechanica.engine.unit.vector.DynamicVector

abstract class Font {
    abstract val atlas: Image

    abstract val ascent: Float
    abstract val descent: Float
    abstract val lineGap: Float
    val lineHeight: Float
        get() = ascent - descent+lineGap

    abstract fun addCharacterDataToArrays(cursor: CharacterCursor, positions: Array<DynamicVector>, texCoords: Array<DynamicVector>)

    companion object {
        fun create(resource: Resource, configureAtlas: FontAtlasConfiguration.() -> Unit = { }): Font {
            return GLLoader.fontLoader.font(resource, configureAtlas)
        }
    }
}