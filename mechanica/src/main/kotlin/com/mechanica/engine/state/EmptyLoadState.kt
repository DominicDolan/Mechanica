package com.mechanica.engine.state

import com.mechanica.engine.drawer.Drawer

object EmptyLoadState : LoadState() {
    override fun preLoad() {}
    override fun renderLoadScreen(draw: Drawer) {}
    override fun load() {}
}