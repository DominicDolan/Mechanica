package com.mechanica.engine.duke

import com.duke.ui.hierarchy.NodeData
import com.duke.ui.output.RenderDescription
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.duke.elements.ElementDrawer
import com.mechanica.engine.duke.style.Style


fun RenderDescription.defaultDraw(draw: Drawer, style: Style) {
    if (style.color.a > 0.0 && style.isVisible) {
        draw.ui.color(style.color).rectangle(x, y, width, height)
    }
}

class NodeRendererToDrawer(private val drawOperation: ElementDrawer) : NodeData.Renderer {
    private val dataToDrawer = NodeDataToDrawer()
    override fun render(e: NodeData) {
        dataToDrawer.render(e) {drawer, style ->
            with(drawOperation) {
                draw(drawer, style)
            }
        }
    }

}

class NodeDataToDrawer {
    inline fun render(e: NodeData, drawOperation: RenderDescription.(draw: Drawer, style: Style) -> Unit) {
        val draw = e.contextData
        val d = e.description
        val style = e.nodeData ?: defaultStyle

        if (draw is Drawer && style is Style) {
            drawOperation(d, draw, style)
        }
    }

    companion object {
        val defaultStyle = Style()
    }
}