package com.mechanica.engine.shaders.models

import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.attributes.Vec2AttributeArray
import com.mechanica.engine.shaders.text.Font
import com.mechanica.engine.shaders.text.Text
import com.mechanica.engine.shaders.utils.ElementIndexArray
import com.mechanica.engine.shaders.utils.createIndicesArrayForQuads
import kotlin.math.max

class TextModel(text: Text) : Model(
        AttributeArray.createPositionArray(text.positions),
        AttributeArray.createTextureArray(text.texCoords),
        ElementIndexArray(max(text.positions.size/2, 20)),
        Image.invoke(text.font.atlas.id),
) {
    private val positionAttribute = inputs[0] as Vec2AttributeArray
    private val texCoordsAttribute = inputs[1] as Vec2AttributeArray
    private val indexArray = inputs[2] as ElementIndexArray

    private var atlas
        get() = inputs[3] as Image
        set(value) {
            inputs[3] = value
        }

    var textHolder: Text = text
        set(value) {
            atlas = value.font.atlas
            field = value
        }

    var string: String
        get() = textHolder.string
        set(value) {
            textHolder.string = value
            updateTextHolder(textHolder)
        }

    val lineCount: Int
        get() = textHolder.lineCount

    constructor(text: String): this(Text(text))
    constructor(text: String, font: Font): this(Text(text, font))

    init {
        vertexCount = textHolder.vertexCount
    }

    fun setText(text: Text) {
        this.textHolder = text
        updateTextHolder(text)
    }

    private fun updateTextHolder(text: Text) {
        positionAttribute.value = text.positions
        positionAttribute.updateBuffer()

        texCoordsAttribute.value = text.texCoords
        texCoordsAttribute.updateBuffer()

        vertexCount = text.vertexCount

        if (vertexCount > indexArray.vertexCount) {
            indexArray.redefineBuffer(createIndicesArrayForQuads(vertexCount))
        }
    }

    fun getLine(index: Int) = textHolder.getLine(index)

    fun getClosestCharacterPosition(x: Double, line: Int) = textHolder.getClosestCharacterPosition(x, line)

    fun getEndOfLinePosition(line: Int) = textHolder.getEndOfLinePosition(line)

    fun getCharacterIndex(x: Double, line: Int) = textHolder.searchCharacter(x, line)

    fun getCharacterPosition(index: Int) = textHolder.getCharacterPosition(index)

}