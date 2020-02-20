package state

import drawer.Drawer

object EmptyLoadState : LoadState() {
    override fun preLoad() {}
    override fun renderLoadScreen(g: Drawer) {}
    override fun load() {}
}