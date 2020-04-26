package drawer.superclass.rectangle

import drawer.Drawer2
import drawer.shader.DrawerRenderer
import gl.models.Model
import gl.vbo.VBO
import util.units.LightweightVector

internal class RectangleDrawerImpl(
        private val matrices: Drawer2.Matrices,
        private val renderer: DrawerRenderer) : RectangleDrawer {

    private val model: Model

    init {
        val position = VBO.createUnitSquarePositionAttribute()
        model = Model(position)
    }

    override fun rectangle() {
        renderer.size.x = matrices.data.scaleX.toDouble()
        renderer.size.y = matrices.data.scaleY.toDouble()
        Drawer2.draw(model, renderer, matrices)
    }

    override fun rectangle(x: Number, y: Number) {
        matrices.data.setTranslate(x.toFloat(), y.toFloat())
        rectangle()
    }

    override fun rectangle(x: Number, y: Number, width: Number, height: Number) {
        matrices.data.setTranslate(x.toFloat(), y.toFloat())
        matrices.data.setScale(width.toFloat(), height.toFloat())
        rectangle()
    }

}