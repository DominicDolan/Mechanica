package com.mechanica.engine.ui

import com.dubulduke.ui.UIContext
import com.dubulduke.ui.element.Element
import com.mechanica.engine.drawer.Drawer

class MechanicaUI(private val context: UIContext<Style, Events>) {
    private val element: UIContext<Style, Events>.BaseElement by lazy { context.createBaseElement() }
    val baseElement: Element<Style, Events>
        get() = element

    inline operator fun invoke(draw: Drawer, block: Element<Style, Events>.() -> Unit) {
        setDrawer(draw)
        block(baseElement)

        renderLayout()
    }

    fun setDrawer(draw: Drawer) {
        context.setUserData(draw)
    }

    fun renderLayout() {
        element.renderLayout(0)
    }
}