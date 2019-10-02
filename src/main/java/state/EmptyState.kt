package state

import renderer.Painter

object EmptyState : State(){
    override fun update(delta: Double) { }
    override fun render(g: Painter) { }
}