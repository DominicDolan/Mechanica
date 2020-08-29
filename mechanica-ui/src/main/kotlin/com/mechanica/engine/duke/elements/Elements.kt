package com.mechanica.engine.duke.elements

import com.duke.ui.output.RenderDescription
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.duke.NodeDataToDrawer
import com.mechanica.engine.models.Image
import com.mechanica.engine.text.Text

inline fun Element.e(content: Element.() -> Unit) {
    switchElementAndAddChild(content) { DefaultElement(it) }
}

inline fun Element.image(image: Image? = null, content: ImageElement.() -> Unit) {
    switchElement({ ImageElement(it) }, { addChildToImageElement(image, content) })
}

fun Element.text(text: String?) {
    val style = this.style
    switchElement({ TextElement(it) }, { addChildToTextElement(text, style) })
}

fun Element.text(text: Text) {
    val style = this.style
    switchElement({ TextElement(it) }, { addChildToTextElement(text, style) })
}

inline fun Element.div(content: Element.() -> Unit) {
    e {
        layout.edit { _, s -> top = s.bottom }
        content()
    }
}

inline fun Element.span(content: Element.() -> Unit) {
    e {
        layout.edit { _, s -> left = s.right }
        content()
    }
}

fun Element.custom(drawer: ElementDrawer) {
    val e = switchElement { CustomElement(it) }
    e.elementDrawer = drawer
    node = e.node
}

val nodeDataToDrawer = NodeDataToDrawer()

inline fun Element.drawer(content: RenderDescription.(draw: Drawer) -> Unit) {
    this.node.renderOnce()
    nodeDataToDrawer.render(this.node) {draw, _ ->
        content(draw)
    }
}


