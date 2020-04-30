package com.mechanica.engine.state

import com.mechanica.engine.drawer.Drawer

object EmptyState : State(){
    override fun update(delta: Double) { }
    override fun render(draw: Drawer) { }
}