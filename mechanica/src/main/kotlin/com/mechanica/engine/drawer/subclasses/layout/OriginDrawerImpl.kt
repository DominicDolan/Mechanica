package com.mechanica.engine.drawer.subclasses.layout

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.drawer.Drawer

class OriginDrawerImpl(drawer: Drawer, private val data: DrawData) : OriginDrawer, Drawer by drawer {

    override fun normalized(x: Number, y: Number): Drawer {
        data.normalizedOrigin.set(x.toDouble(), y.toDouble())
        return this
    }

    override fun relative(x: Number, y: Number): Drawer {
        data.relativeOrigin.set(x.toDouble(), y.toDouble())
        return this
    }
}