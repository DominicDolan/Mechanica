package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer

interface SceneNode : Activatable {
    fun onRemove() {}
}

interface Updateable : SceneNode {
    fun update(delta: Double)
}

interface Renderable : SceneNode {
    fun render(draw: Drawer)
}