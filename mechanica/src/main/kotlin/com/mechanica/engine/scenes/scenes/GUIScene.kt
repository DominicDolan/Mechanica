package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.View

abstract class GUIScene : Scene() {
    override val view: View
        get() = Game.ui
    override val Drawer.inScene: Drawer
        get() = drawInScene(this, view).ui
}