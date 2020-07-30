package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.UIView
import com.mechanica.engine.game.view.View

abstract class MainUIScene(order: Int = 1) : MainScene(order) {
    override val view: UIView
        get() = Game.ui

    override fun drawInScene(draw: Drawer, view: View): Drawer {
        return super.drawInScene(draw, view).ui
    }
}