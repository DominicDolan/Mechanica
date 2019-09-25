package state

import renderer.Painter

object EmptyLoadState : LoadState() {
    override fun preLoad() {}
    override fun renderLoadScreen(g: Painter) {}
    override fun load() {}
}