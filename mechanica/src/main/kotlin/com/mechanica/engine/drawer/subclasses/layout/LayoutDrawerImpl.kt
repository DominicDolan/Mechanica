package com.mechanica.engine.drawer.subclasses.layout

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.drawer.Drawer

class LayoutDrawerImpl(drawer: Drawer, private val data: DrawData) : LayoutDrawer, Drawer by drawer {

    override fun origin(): Drawer {
        data.modelOrigin.set(0.0, 0.0)
        return this
    }

    override fun origin(x: Number, y: Number): Drawer {
        data.modelOrigin.set(x.toDouble(), y.toDouble())
        return this
    }
}