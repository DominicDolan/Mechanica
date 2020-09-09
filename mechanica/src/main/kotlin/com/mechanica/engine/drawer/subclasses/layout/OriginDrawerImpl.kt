package com.mechanica.engine.drawer.subclasses.layout

import com.mechanica.engine.drawer.DrawData
import com.mechanica.engine.drawer.Drawer

class OriginDrawerImpl(drawer: Drawer, private val data: DrawData) : OriginDrawer, Drawer by drawer {

    override fun relative(x: Number, y: Number): Drawer {
        data.relativeOrigin.set(x.toDouble(), y.toDouble())
        return this
    }

    override fun absolute(x: Number, y: Number): Drawer {
        data.absoluteOrigin.set(x.toDouble(), y.toDouble())
        return this
    }
}