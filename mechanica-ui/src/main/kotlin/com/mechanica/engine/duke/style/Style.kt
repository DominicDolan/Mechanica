package com.mechanica.engine.duke.style

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.DynamicColor
import com.mechanica.engine.text.Font
import com.mechanica.engine.unit.vector.DynamicVector

class Style {
    var color: Color = DynamicColor(1.0, 1.0, 1.0, 0.0)
        set(value) {
            (field as DynamicColor).set(value)
        }
    var isVisible: Boolean = true

    val textFormat = TextFormat()

    var radius: Double = 0.0

    inline fun edit(editor: Style.() -> Unit) {
        editor(this)
    }

    class TextFormat {
        var color: Color = DynamicColor(0.7, 0.7, 0.7, 1.0)
            set(value) {
                (field as DynamicColor).set(value)
            }
        var size: Double = 1.0

        val alignment: DynamicVector = DynamicVector.create(0.0, 0.0)

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