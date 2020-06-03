package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.processes.ProcessNode

interface SceneNode : ProcessNode, Drawable {
    val view: View
    fun <S:Scene> addScene(scene: S): S
    fun removeScene(scene: Scene): Boolean
    fun <S:Scene> replaceScene(old: S, new: S): S
}