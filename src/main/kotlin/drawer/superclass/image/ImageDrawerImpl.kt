package drawer.superclass.image

import drawer.DrawData
import drawer.Drawer
import drawer.shader.DrawerRenderer
import gl.models.ImageModel
import gl.vbo.VBO
import graphics.Image

class ImageDrawerImpl(
        private val data: DrawData) : ImageDrawer {

    private val model: ImageModel

    init {
        val position = VBO.createUnitSquarePositionAttribute()
        VBO.createUnitSquareTextureAttribute()
        val textureCoords = VBO.createUnitSquareTextureAttribute()
        model = ImageModel(Image(0), position, textureCoords)
    }

    override fun image(image: Image) {
        model.image = image

        data.cornerSize.x = data.scaleX.toDouble()
        data.cornerSize.y = data.scaleY.toDouble()

        data.blend = 1f
        data.draw(model)
        data.blend = 0f
    }

    override fun image(image: Image, x: Number, y: Number) {
        data.setTranslate(x.toFloat(), y.toFloat())
        image(image)
    }

    override fun image(image: Image, x: Number, y: Number, width: Number, height: Number) {
        data.setTranslate(x.toFloat(), y.toFloat())
        data.setScale(width.toFloat(), height.toFloat())

        data.cornerSize.x = width.toDouble()
        data.cornerSize.y = height.toDouble()

        image(image)
    }

}