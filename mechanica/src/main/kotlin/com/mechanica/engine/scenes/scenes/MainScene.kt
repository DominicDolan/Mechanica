package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.GameView

abstract class MainScene(order: Int = 0) : Scene(order) {
    abstract override val view: GameView

    fun setNewMainScene(setter: () -> MainScene?) = Game.setMainScene(setter)
}