package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.WorldView

abstract class WorldScene(order: Int = 0) : MainScene(order) {
    override val view: WorldView
        get() = Game.view
}