package com.mechanica.engine.drawer.superclass.image

import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.models.Image
import com.mechanica.engine.models.ImageModel
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.vertices.AttributeArray

class ImageDrawerImpl(
        private val state: DrawState) : ImageDrawer {

    private val model: ImageModel

    init {
        val position = AttributeArray.createFrom(Attribute.position).createUnitQuad()
        val textureCoords = AttributeArray.createFrom(Attribute.textureCoords).createInvertedUnitQuad()

        model = ImageModel(Image(0), position, textureCoords)
    }

    override fun image(image: Image, x: Number, y: Number, width: Number, height: Number) {
        setImage(image, x, y, width, height)
        drawImage(1f, false)
    }

    override fun colorizedImage(image: Image, x: Number, y: Number, width: Number, height: Number) {
        setImage(image, x, y, width, height)
        drawImage(0f, true)
    }

    private fun setImage(image: Image, x: Number, y: Number, width: Number, height: Number) {
        model.image = image

        state.setTranslate(x.toFloat(), y.toFloat())
        state.setScale(width.toFloat(), height.toFloat())

        state.cornerSize.x = width.toDouble()
        state.cornerSize.y = height.toDouble()

    }

    private fun drawImage(blend: Float, colorPassthrough: Boolean) {
        state.blend = blend
        state.alphaBlend = 1f
        state.colorPassthrough = colorPassthrough

        state.draw(model)

        state.blend = 0f
        state.alphaBlend = 0f
        state.colorPassthrough = false
    }

}