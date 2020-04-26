package drawer.superclass.text

import drawer.Drawer2
import drawer.shader.DrawerRenderer
import gl.models.TextModel
import util.extensions.vec
import util.units.LightweightVector

class TextDrawerImpl(
        private val matrices: Drawer2.Matrices,
        private val renderer: DrawerRenderer) : TextDrawer {

    override fun text(text: String) {
        val model = matrices.data.textModel
        model.text = text
        renderer.blend = 0f
        renderer.alphaBlend = 1f
        renderer.colorPassthrough = true

        val topLeft = vec(0.0, -1.0)
        val bottomRight = bottomRight(model)

        val height = bottomRight.y - topLeft.y
        val width = bottomRight.x - topLeft.x

        val origin = matrices.data.modelOrigin
        val oX = origin.x
        val oY = origin.y

        origin.set(oX*width, oY*height - bottomRight.y)
        Drawer2.draw(model, renderer, matrices)
        origin.set(oX, oY)
    }

    override fun text(text: String, size: Number, x: Number, y: Number) {
        matrices.data.setTranslate(x.toFloat(), y.toFloat())
        matrices.data.setScale(size.toFloat(), size.toFloat())
        text(text)
    }

    private fun bottomRight(model: TextModel): LightweightVector {
        val lc = model.lineCount
        val y = lc.toDouble()
        var longest = 0.0
        for (i in 0 until lc) {
            val length = model.getEndOfLinePosition(i)
            if (length > longest) {
                longest = length
            }
        }

        val x = longest

        return vec(x, y)
    }
}