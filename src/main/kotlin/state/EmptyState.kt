package state

import graphics.drawer.Drawer

object EmptyState : State(){
    override fun update(delta: Double) { }
    override fun render(draw: Drawer) { }
}