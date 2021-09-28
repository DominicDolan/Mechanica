package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.scenes.activation.Activatable

interface SceneNode : Activatable {
    fun onRemove() {}
}

interface Updateable : SceneNode {
    fun update(delta: Double)
}

interface Renderable : SceneNode {
    fun render(draw: Drawer)
}