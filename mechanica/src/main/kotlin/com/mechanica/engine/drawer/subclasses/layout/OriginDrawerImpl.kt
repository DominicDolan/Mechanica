package com.mechanica.engine.drawer.subclasses.layout

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.state.DrawState

class OriginDrawerImpl(drawer: Drawer, private val state: DrawState) : OriginDrawer, Drawer by drawer {

    override fun normalized(x: Number, y: Number): Drawer {
        state.origin.normalized.set(x.toDouble(), y.toDouble())
        return this
    }

    override fun relative(x: Number, y: Number): Drawer {
        state.origin.relative.set(x.toDouble(), y.toDouble())
        return this
    }
}