package com.mechanica.engine.drawer.subclasses.layout

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.drawer.Drawer

class OriginDrawerImpl(drawer: Drawer, private val data: DrawData) : OriginDrawer, Drawer by drawer {

    override fun relative(): Drawer {
        data.modelOrigin.set(0.0, 0.0)
        return this
    }

    override fun relative(x: Number, y: Number): Drawer {
        data.modelOrigin.set(x.toDouble(), y.toDouble())
        return this
    }
}