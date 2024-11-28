package com.mechanica.engine.samples.ui.duke.theme

import com.cave.library.color.VariableColor
import com.cave.library.vector.vec2.MutableVector2
import com.mechanica.engine.shaders.text.Font

open class Style {
    open val color: VariableColor = VariableColor.rgba(1.0, 1.0, 1.0, 0.0)

    open var isVisible: Boolean = true

    open val textFormat = TextFormat()

    open var radius: Double = 0.0

    inline fun edit(editor: Style.() -> Unit) {
        editor(this)
    }

    open class TextFormat {
        open val color: VariableColor = VariableColor.rgba(0.7, 0.7, 0.7, 1.0)

        open var size: Double = 1.0

        open val alignment: MutableVector2 = MutableVector2.create(0.0, 0.0)

        open var font: Font? = null

        inline fun edit(editor: TextFormat.() -> Unit) {
            editor(this)
        }
    }

    companion object {
        fun copy(source: Style, destination: Style) {
            destination.color.set(source.color)
        }
    }
}
