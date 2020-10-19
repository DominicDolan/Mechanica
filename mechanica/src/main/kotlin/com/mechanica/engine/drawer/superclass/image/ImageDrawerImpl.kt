package com.mechanica.engine.drawer.superclass.image

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.models.Image
import com.mechanica.engine.models.ImageModel
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.vertices.AttributeArray

class ImageDrawerImpl(
        private val data: DrawData) : ImageDrawer {

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

        data.setTranslate(x.toFloat(), y.toFloat())
        data.setScale(width.toFloat(), height.toFloat())

        data.cornerSize.x = width.toDouble()
        data.cornerSize.y = height.toDouble()

    }

    private fun drawImage(blend: Float, colorPassthrough: Boolean) {
        data.blend = blend
        data.alphaBlend = 1f
        data.colorPassthrough = colorPassthrough

        data.draw(model)

        data.blend = 0f
        data.alphaBlend = 0f
        data.colorPassthrough = false
    }

}