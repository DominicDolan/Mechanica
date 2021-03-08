package com.mechanica.engine.duke.style

import com.cave.library.color.VariableColor
import com.cave.library.vector.vec2.VariableVector2
import com.mechanica.engine.shaders.text.Font

class Style {
    val color: VariableColor = VariableColor.rgba(1.0, 1.0, 1.0, 0.0)

    var isVisible: Boolean = true

    val textFormat = TextFormat()

    var radius: Double = 0.0

    inline fun edit(editor: Style.() -> Unit) {
        editor(this)
    }

    class TextFormat {
        val color: VariableColor = VariableColor.rgba(0.7, 0.7, 0.7, 1.0)

        var size: Double = 1.0

        val alignment: VariableVector2 = VariableVector2.create(0.0, 0.0)

        var font: Font? = null

        inline fun edit(editor: TextFormat.() -> Unit) {
            editor(this)
        }
    }

    companion object {
        fun createStyleSetter(block: Style.() -> Unit) = Styler(block)
    }
}

fun interface Styler {
    fun Style.set()
}