package state

import graphics.drawer.Drawer

object EmptyLoadState : LoadState() {
    override fun preLoad() {}
    override fun renderLoadScreen(g: Drawer) {}
    override fun load() {}
}