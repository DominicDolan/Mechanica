package drawer.superclass.image

import drawer.Drawer
import drawer.shader.DrawerRenderer
import gl.models.ImageModel
import gl.vbo.VBO
import graphics.Image

class ImageDrawerImpl(
        private val matrices: Drawer.Matrices,
        private val renderer: DrawerRenderer) : ImageDrawer {

    private val model: ImageModel

    init {
        val position = VBO.createUnitSquarePositionAttribute()
        VBO.createUnitSquareTextureAttribute()
        val textureCoords = VBO.createUnitSquareTextureAttribute()
        model = ImageModel(Image(0), position, textureCoords)
    }

    override fun image(image: Image) {
        model.image = image

        renderer.size.x = matrices.data.scaleX.toDouble()
        renderer.size.y = matrices.data.scaleY.toDouble()

        renderer.blend = 1f
        Drawer.draw(model, renderer, matrices)
        renderer.blend = 0f
    }

    override fun image(image: Image, x: Number, y: Number) {
        matrices.data.setTranslate(x.toFloat(), y.toFloat())
        image(image)
    }

    override fun image(image: Image, x: Number, y: Number, width: Number, height: Number) {
        matrices.data.setTranslate(x.toFloat(), y.toFloat())
        matrices.data.setScale(width.toFloat(), height.toFloat())
        renderer.size.x = width.toDouble()
        renderer.size.y = height.toDouble()
        image(image)
    }

}