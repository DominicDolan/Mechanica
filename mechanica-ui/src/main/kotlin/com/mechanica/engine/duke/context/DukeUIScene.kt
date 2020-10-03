package com.mechanica.engine.duke.context

import com.duke.ui.hierarchy.DukeUI
import com.duke.ui.hierarchy.NodeData
import com.duke.ui.hierarchy.Viewport
import com.duke.ui.hierarchy.Window
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.duke.NodeRendererToDrawer
import com.mechanica.engine.duke.defaultDraw
import com.mechanica.engine.duke.elements.Element
import com.mechanica.engine.duke.elements.ElementDrawer
import com.mechanica.engine.scenes.scenes.UIScene
import com.mechanica.engine.util.extensions.fori

abstract class DukeUIScene : UIScene() {
    private val context: DukeUI
    internal val firstNode: NodeData
        get() = context.firstElement

    val list = ArrayList<Element>()

    init {
        val viewport = Viewport(0.0, 0.0, camera.width, -camera.height)
        val window = Window(-camera.width/2.0, -camera.height/2.0, camera.width, camera.height)

        context = DukeUI(window, viewport)

        context.defaultRender = NodeRendererToDrawer { draw, style -> defaultDraw(draw, style) }
    }

    override fun render(draw: Drawer) {
        context.data = draw
        context.ui { getElement { OuterElement() }.ui() }
    }

    inline fun <reified E : Element> getElement(initiator: (DukeUIScene) -> E): E {
        list.fori {
            if (it is E) return it
        }
        val new = initiator(this)
        list.add(new)
        return new
    }

    abstract fun Element.ui()

    private inner class OuterElement : Element(this) {
        override val elementDrawer = ElementDrawer {_, _ ->  }
    }
}