package drawer.superclass.circle

import drawer.Drawer
import drawer.shader.DrawerRenderer
import gl.models.Model
import gl.vbo.VBO
import util.units.LightweightVector

class CircleDrawerImpl(
        private val matrices: Drawer.Matrices,
        private val renderer: DrawerRenderer): CircleDrawer {

    private val model: Model

    init {
        val position = VBO.createUnitSquarePositionAttribute()
        model = Model(position)
    }

    override fun circle() {
        val radius = if (matrices.data.radius > 0.0) matrices.data.radius else 1.0
        val diameter = radius*2.0

        matrices.data.setScale(diameter.toFloat(), diameter.toFloat())
        renderer.size.set(diameter, diameter)

        val origin = matrices.data.modelOrigin
        if (!origin.wasSet) {
            origin.set(0.5, 0.5)
        }

        Drawer.draw(model, renderer, matrices)
    }

    override fun circle(x: Number, y: Number) {
        matrices.data.setTranslate(x.toFloat(), y.toFloat())
        circle()
    }

    override fun circle(x: Number, y: Number, radius: Number) {
        TODO("not implemented")
    }

    override fun ellipse(x: Number, y: Number, width: Number, height: Number) {
        TODO("not implemented")
    }

    override fun ellipse(xy: LightweightVector, wh: LightweightVector) {
        TODO("not implemented")
    }
}