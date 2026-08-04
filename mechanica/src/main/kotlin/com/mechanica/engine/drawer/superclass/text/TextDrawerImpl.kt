package com.mechanica.engine.drawer.superclass.text

import com.cave.library.vector.vec2.InlineVector
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.drawer.shader.DrawerRenderer
import com.mechanica.engine.drawer.state.DrawState
import com.mechanica.engine.game.Game
import com.mechanica.engine.shaders.models.TextModel
import com.mechanica.engine.shaders.text.Text

class TextDrawerImpl(
        private val state: DrawState,
        private val renderer: DrawerRenderer) : TextDrawer {

    override fun text(text: String) {
        val model = state.stringHolderModel
        model.string = text
        drawText(model)
    }

    override fun text(text: Text) {
        val model = state.textHolderModel
        model.setText(text)
        drawText(model)
    }

    private fun drawText(model: TextModel) {
        if (!state.viewMatrix.wasChanged) state.viewMatrix.variable = Game.matrices.uiCamera

        val topLeft = vec(0.0, -1.0)
        val bottomRight = bottomRight(model)

        val height = bottomRight.y - topLeft.y
        val width = bottomRight.x - topLeft.x

        val origin = state.origin.normalized
        val oX = origin.x
        val oY = origin.y

        origin.set(oX*width, oY*height - bottomRight.y)

        state.setModel(model)
        renderer.render(state, 0f, 1f, true)
    }


    override fun text(text: Text, size: Number, x: Number, y: Number) {
        positionAndScale(size, x, y)
        text(text)
    }

    override fun text(text: String, size: Number, x: Number, y: Number) {
        positionAndScale(size, x, y)
        text(text)
    }

    private fun positionAndScale(size: Number, x: Number, y: Number) {
        state.setTranslate(x.toDouble(), y.toDouble())
        state.setScale(size.toDouble(), size.toDouble())
    }

    private fun bottomRight(model: TextModel): InlineVector {
        val lc = model.lineCount
        // Each line after the first advances by the font's line height — that is what
        // `Text.addNewLine` writes into `yAdvance` — while the first line occupies the em box,
        // `ascent - descent`, which is 1.0 by construction because the metrics are scaled by
        // `stbtt_ScaleForPixelHeight(info, 1f)`.
        //
        // This used to be `lc`, one unit per line, which under-measures multi-line text by the
        // line gap per line: the box stopped containing the glyphs it was boxing, so anything
        // anchored with `origin.normalized` drifted as the line count grew. Single-line text is
        // unaffected — for `lc == 1` this is the same `1.0` it was before.
        val y = (lc - 1) * model.textHolder.font.lineHeight.toDouble() + 1.0
        var longest = 0.0
        for (i in 0 until lc) {
            val length = model.getEndOfLinePosition(i)
            if (length > longest) {
                longest = length
            }
        }

        val x = longest

        return vec(x, y - 1.0)
    }
}