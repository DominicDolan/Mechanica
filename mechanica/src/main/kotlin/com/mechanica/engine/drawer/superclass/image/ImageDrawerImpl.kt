package com.mechanica.engine.drawer.superclass.image

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.shader.qualifiers.Attribute
import com.mechanica.engine.models.Image
import com.mechanica.engine.models.ImageModel

class ImageDrawerImpl(
        private val data: DrawData) : ImageDrawer {

    private val model: ImageModel

    init {
        val position = Attribute(0).vec3().createUnitQuad()
        val textureCoords = Attribute(1).vec2().createInvertedUnitQuad()

        model = ImageModel(Image(0), position, textureCoords)
    }

    override fun image(image: Image, x: Number, y: Number, width: Number, height: Number) {
        data.setTranslate(x.toFloat(), y.toFloat())
        data.setScale(width.toFloat(), height.toFloat())

        data.cornerSize.x = width.toDouble()
        data.cornerSize.y = height.toDouble()

        drawImage(image)
    }

    private fun drawImage(image: Image) {
        model.image = image

        data.cornerSize.x = data.scaleX.toDouble()
        data.cornerSize.y = data.scaleY.toDouble()

        data.blend = 1f
        data.alphaBlend = 1f
        data.draw(model)
        data.blend = 0f
        data.alphaBlend = 0f
    }

}