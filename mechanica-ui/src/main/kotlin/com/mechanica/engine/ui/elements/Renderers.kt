package com.mechanica.engine.ui.elements

import com.dubulduke.ui.UIContext
import com.dubulduke.ui.render.ElementRenderer
import com.dubulduke.ui.render.RenderDescription
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.text.Text
import com.mechanica.engine.ui.Events
import com.mechanica.engine.ui.style.Style


interface ElementDrawer : ElementRenderer<Style, Events, DrawerElement> {
    override fun draw(description: RenderDescription<Style>, data: Any?) {
        if (data != null && data is Drawer) {
            description.draw(data.ui)
        }
    }

    override fun createElement(context: UIContext<Style, Events>): DrawerElement {
        return DrawerElement(context, this)
    }

    fun RenderDescription<Style>.draw(draw: Drawer)
}

class BoxRenderer : ElementDrawer {
    override fun RenderDescription<Style>.draw(draw: Drawer) {
        draw.ui.color(style.color).rectangle(x, y, width, height)
    }
}


class TextElementRenderer(private var text: Text) : ElementDrawer {
    var string: String
        get() = text.string
        set(value) {
            text.string = value
        }

    override fun RenderDescription<Style>.draw(draw: Drawer) {
        draw.ui.color(style.textColor).origin.relative(0.0, 1.0).text(text, style.fontSize, x, y)
    }
//
//    private fun getText(): Text {
//        val text = text
//        val font = font
//        val newText = when {
//            text == null -> createText(string)
//            font != null && text.font !== font -> createText(string)
//            text.string != string -> {
//                text.string = string
//                text
//            }
//            else -> text
//        }
//        this.text = newText
//        return newText
//    }
//
//    private fun createText(string: String): Text {
//        val font = this.font
//        val text = if (font != null) {
//            Text(string, font)
//        } else {
//            Text(string)
//        }
//        this.text = text
//        return text
//    }

    override fun createElement(context: UIContext<Style, Events>): DrawerElement {
        return TextElement(context, this)
    }
}
