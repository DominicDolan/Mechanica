package com.mechanica.engine.duke.elements

import com.duke.ui.hierarchy.NodeData
import com.duke.ui.output.RenderDescription
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.duke.NodeDataToDrawer
import com.mechanica.engine.duke.style.Style
import com.mechanica.engine.models.Image
import com.mechanica.engine.text.Text

inline fun NodeData.div(block: NodeData.() -> Unit) {
    val e = this.insertNode()
    block(e)
}

inline fun NodeData.span(block: NodeData.() -> Unit) {
    block(insertNode())
}


inline fun Element.e(block: Element.() -> Unit) {
    val e = switchElement { DefaultElement(it) }
    e.addChild(block)
}

inline fun Element.image(image: Image? = null, block: ImageElement.() -> Unit) {
    val e = switchElement { ImageElement(it) }
    e.addChildToImageElement(image, block)
}

fun Element.text(text: String?) {
    val style = this.style
    val e = switchElement { TextElement(it) }
    e.addChildToTextElement(text, style)
}

fun Element.text(text: Text) {
    val style = this.style
    val e = switchElement { TextElement(it) }
    e.addChildToTextElement(text, style)
}

inline fun Element.div(block: Element.() -> Unit) {
    e {
        layout.edit { p, s ->
            top = s.bottom
            block()
        }
    }
}

fun Element.custom(drawer: ElementDrawer) {
    val e = switchElement { CustomElement(it) }
    e.elementDrawer = drawer
}

val nodeDataToDrawer = NodeDataToDrawer()

inline fun Element.drawer(block: RenderDescription.(draw: Drawer, style: Style) -> Unit) {
    this.node.renderOnce()
    nodeDataToDrawer.render(this.node, block)
}
