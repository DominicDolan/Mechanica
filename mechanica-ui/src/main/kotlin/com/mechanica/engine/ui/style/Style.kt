package com.mechanica.engine.ui.style

import com.mechanica.engine.color.DynamicColor
import com.mechanica.engine.text.Font

class Style {
    val color: DynamicColor = DynamicColor(1.0, 1.0, 1.0, 0.0)
    val textColor: DynamicColor = DynamicColor(0.0, 0.0, 0.0, 1.0)
    var text: String = ""
    var fontSize: Double = 1.0
    var font: Font? = null
    var radius: Double = 0.0

    fun add(styleSetter: StyleSetter) {
        styleSetter.set(this)
    }

    fun add(styleSetter1: StyleSetter, styleSetter2: StyleSetter) {
        add(styleSetter1)
        add(styleSetter2)
    }

    fun add(styleSetter1: StyleSetter, styleSetter2: StyleSetter, styleSetter3: StyleSetter) {
        add(styleSetter1)
        add(styleSetter2)
        add(styleSetter3)
    }

    fun add(styleSetter1: StyleSetter, styleSetter2: StyleSetter, styleSetter3: StyleSetter, styleSetter4: StyleSetter) {
        add(styleSetter1)
        add(styleSetter2)
        add(styleSetter3)
        add(styleSetter4)
    }

    companion object {
        operator fun invoke(setter: Style.() -> Unit) = object : StyleSetter {
            override fun set(style: Style) {
                setter.invoke(style)
            }

        }
    }
}