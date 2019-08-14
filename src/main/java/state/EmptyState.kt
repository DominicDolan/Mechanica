package state

import renderer.Painter

object EmptyState : State(){
    override fun update(delta: Float) { }
    override fun render(g: Painter) { }
}