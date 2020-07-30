package com.mechanica.engine.ui

import com.dubulduke.ui.UIContext
import com.dubulduke.ui.element.Element
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.ui.elements.BoxElement
import com.mechanica.engine.ui.elements.DrawerElement
import com.mechanica.engine.ui.style.Style

class MechanicaUI(private val context: UIContext<Style, Events>) {
    private val element: UIContext<Style, Events>.BaseElement by lazy { context.createBaseElement() }
    val baseElement: Element<Style, Events>
        get() = element
    val baseDrawer: DrawerElement = BoxElement(context)

    inline operator fun invoke(draw: Drawer, block: DrawerElement.() -> Unit) {
        setDrawer(draw)
        val element = baseElement.addChildElement { baseDrawer }
        block(element)

        renderLayout()
    }

    fun setDrawer(draw: Drawer) {
        context.setUserData(draw)
    }

    fun renderLayout() {
        element.renderLayout(0)
    }
}