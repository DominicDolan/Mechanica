package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.GameView

abstract class MainScene : Scene() {
    override val view: GameView
        get() = Game.view
}