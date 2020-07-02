package com.mechanica.engine.ui

import com.dubulduke.ui.UIContext
import com.dubulduke.ui.element.Element
import com.dubulduke.ui.render.ElementRenderer
import com.dubulduke.ui.render.RenderDescription
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.text.Font
import com.mechanica.engine.text.Text

class DrawerElement(context: UIContext<Style, Events>, renderer: ElementRenderer<Style, Events>)
    : Element<Style, Events>(context, renderer) {

    fun String.unaryPlus() {
        text(this)
    }

}

abstract class ElementDrawer : ElementRenderer<Style, Events> {
    override fun draw(description: RenderDescription<Style>, data: Any?) {
        if (data != null && data is Drawer) {
            description.draw(data.ui)
        }
    }

    override fun createElement(context: UIContext<Style, Events>): DrawerElement {
        return DrawerElement(context, this)
    }

    abstract fun RenderDescription<Style>.draw(draw: Drawer)
}

class BoxRenderer : ElementDrawer() {
    override fun RenderDescription<Style>.draw(draw: Drawer) {
        draw.ui.color(style.color).rectangle(x, y, width, height)
    }
}

inline fun Element<Style, Events>.box(block: Element<Style, Events>.() -> Unit) {
    val boxElement = addChild { BoxRenderer() }
    block(boxElement)
}

fun Element<Style, Events>.text(string: String) {
    val text = addChildElement { TextElement(string, context) }
    if (text is TextElement) {
        text.string = string
        text.copyStyle(this.style)
    }

}

class TextElementRenderer(var string: String) : ElementDrawer() {

    private var font: Font? = null
    private var text: Text? = null

    override fun RenderDescription<Style>.draw(draw: Drawer) {
        this@TextElementRenderer.font = this.style.font
        val text = getText()
        draw.ui.color(style.textColor).layout.origin(0.0, 1.0).text(text, style.fontSize, x, y)
    }

    private fun getText(): Text {
        val text = text
        val font = font
        val newText = when {
            text == null -> createText(string)
            font != null && text.font !== font -> createText(string)
            text.string != string -> {
                text.string = string
                text
            }
            else -> text
        }
        this.text = newText
        return newText
    }

    private fun createText(string: String): Text {
        val font = this.font
        val text = if (font != null) {
            Text(string, font)
        } else {
            Text(string)
        }
        this.text = text
        return text
    }
}

class TextElement(string: String, context: UIContext<Style, Events>) : Element<Style, Events>(context, TextElementRenderer(string)) {
    override val renderer: TextElementRenderer
        get() = super.renderer as TextElementRenderer

    var string: String
        get() = renderer.string
        set(value) {
            renderer.string = value
        }

    fun copyStyle(style: Style) {
        style {
            textColor = style.textColor
            fontSize = style.fontSize
            this.font = style.font
        }
    }
}