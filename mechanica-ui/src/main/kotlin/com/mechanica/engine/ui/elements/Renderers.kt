package com.mechanica.engine.ui.elements

import com.dubulduke.ui.UIContext
import com.dubulduke.ui.render.ElementRenderer
import com.dubulduke.ui.render.RenderDescription
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.models.Image
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

    override fun createElement(context: UIContext<Style, Events>): DrawerElement {
        return TextElement(context, this)
    }
}

class ImageElementRenderer(private val image: Image): ElementDrawer {
    override fun createElement(context: UIContext<Style, Events>): DrawerElement {
        return DrawerElement(context, this)
    }

    override fun RenderDescription<Style>.draw(draw: Drawer) {
        draw.ui.image(image, x, y + height, width, -height)
    }
}
