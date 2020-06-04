package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.processes.ProcessNode

interface SceneNode : ProcessNode, Drawable {
    val view: View
    fun <S:SceneNode> addScene(scene: S): S
    fun removeScene(scene: SceneNode): Boolean
    fun <S:SceneNode> replaceScene(old: S, new: S): S
    fun renderNodes(draw: Drawer)
}