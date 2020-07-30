package com.mechanica.engine.ui.elements

import com.dubulduke.ui.UIContext
import com.dubulduke.ui.element.Element
import com.mechanica.engine.text.Text
import com.mechanica.engine.ui.Events
import com.mechanica.engine.ui.style.Style

open class DrawerElement(context: UIContext<Style, Events>, renderer: ElementDrawer)
    : Element<Style, Events>(context, renderer) {

    operator fun String.unaryPlus() {
        text(this)
    }

}

open class BoxElement(context: UIContext<Style, Events>) : DrawerElement(context, BoxRenderer())


class TextElement(context: UIContext<Style, Events>, textElementRenderer: TextElementRenderer) : DrawerElement(context, textElementRenderer) {

    constructor(text: Text, context: UIContext<Style, Events>) : this(context, TextElementRenderer(text))
    constructor(string: String, context: UIContext<Style, Events>) : this(Text(string), context)

    override val renderer: TextElementRenderer
        get() = super.renderer as TextElementRenderer

    var string: String
        get() = renderer.string
        set(value) {
            renderer.string = value
        }


    fun copyStyle(style: Style) {
        style {
            textColor.set(style.textColor)
            fontSize = style.fontSize
            this.font = style.font
        }
    }
}