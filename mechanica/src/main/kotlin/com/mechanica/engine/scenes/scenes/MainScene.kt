package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.GameView

abstract class MainScene(order: Int = 0) : Scene(order) {
    override val view: GameView
        get() = Game.view

    fun setNewMainScene(setter: () -> MainScene?) = Game.setMainScene(setter)

}