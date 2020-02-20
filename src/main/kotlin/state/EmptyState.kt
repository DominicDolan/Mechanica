package state

import drawer.Drawer

object EmptyState : State(){
    override fun update(delta: Double) { }
    override fun render(draw: Drawer) { }
}