package com.mechanica.engine.duke.elements

import com.duke.ui.hierarchy.NodeData
import com.duke.ui.layout.DynamicLayout
import com.mechanica.engine.duke.NodeRendererToDrawer
import com.mechanica.engine.duke.context.DukeUIScene
import com.mechanica.engine.duke.style.Style
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.text.Text

abstract class Element(val scene: DukeUIScene) {
    var node: NodeData = scene.firstNode

    abstract val elementDrawer: ElementDrawer

    private val nodeToDrawer by lazy { NodeRendererToDrawer(elementDrawer) }

    val layout: DynamicLayout
        get() = node.layout

    val virtualLayout: DynamicLayout
        get() = node.virtualLayout

    open val style: Style
        get() = (node.nodeData as? Style) ?: Style().also { node.nodeData = it }

    val isHovering: Boolean
        get() = node.description.contains(Mouse.ui.x, Mouse.ui.y)

    inline fun <reified E : Element> switchElement(initializer: (DukeUIScene) -> E) : E {
        val new = scene.getElement(initializer)
        new.node = node
        return new
    }

    inline fun addChild(block: Element.() -> Unit) {
        val oldNode = updateForNextNode(node)
        block(this)
        node.renderOnce()
        this.node = oldNode
    }

    fun updateForNextNode(node: NodeData): NodeData {
        val oldNode = this.node
        this.node = node.insertNode()

        oldNode.renderOnce()
        this.node.setRenderer(nodeToDrawer)

        return oldNode
    }

    operator fun String.unaryPlus() {
        text(this)
    }

    operator fun Text.unaryPlus() {
        text(this)
    }

}