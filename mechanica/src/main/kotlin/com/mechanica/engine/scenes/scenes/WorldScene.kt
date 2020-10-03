package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.WorldCamera

abstract class WorldScene(order: Int = 0) : Scene(order) {
    final override val camera: WorldCamera
        get() = Game.world
}