package com.mechanica.engine.drawer.superclass.image

import com.mechanica.engine.drawer.shader.DrawerRenderer
import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.shaders.models.ImageModel
import com.mechanica.engine.shaders.utils.createInvertedUnitSquareVectors
import com.mechanica.engine.shaders.utils.createUnitSquareVectors

class ImageDrawerImpl(
        private val state: DrawState,
        private val renderer: DrawerRenderer) : ImageDrawer {

    private val model: ImageModel

    init {
        val position = AttributeArray.createPositionArray(createUnitSquareVectors())
        val textureCoords = AttributeArray.createTextureArray(createInvertedUnitSquareVectors())

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
        state.setModel(model)

        state.setTranslate(x.toFloat(), y.toFloat())
        state.setScale(width.toFloat(), height.toFloat())

        state.shader.cornerSize.set(width.toDouble(), height.toDouble())
    }

    private fun drawImage(blend: Float, colorPassthrough: Boolean) {
        renderer.render(state, blend, 1f, colorPassthrough)
    }

}