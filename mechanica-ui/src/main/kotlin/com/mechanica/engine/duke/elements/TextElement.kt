package com.mechanica.engine.duke.elements

import com.mechanica.engine.duke.context.DukeUIScene
import com.mechanica.engine.duke.style.Style
import com.mechanica.engine.text.Text

class TextElement(scene: DukeUIScene) : Element(scene) {
    var text = Text("")

    private val emptyString = ""

    override var style: Style
        get() = super.style
        private set(value) {
            node.nodeData = value
        }

    override val elementDrawer = ElementDrawer { draw, style ->
        with(style.textFormat) {
            draw.ui.color(color)
                .origin.relative(alignment.x, 1.0 - alignment.y)
                .text(text, size, x + alignment.x*width, y + alignment.y*height)
        }
    }

    fun addChildToTextElement(text: String?, parentStyle: Style) {
        this.text.update(text ?: emptyString, parentStyle.textFormat.font ?: this.text.font)

        addChildToTextElement(this.text, parentStyle)

        this.text.string = emptyString
    }

    fun addChildToTextElement(text: Text, parentStyle: Style) {
        val oldNode = updateForNextNode(node)
        val oldText = this.text

        this.style = parentStyle
        this.text = text

        val font = style.textFormat.font
        if (font != null) {
            text.update(text.string, font)
        }

        node.renderOnce()

        this.node = oldNode
        this.text = oldText
    }
}