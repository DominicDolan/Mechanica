package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.Camera

abstract class UIScene(order: Int = 1) : Scene(order) {
    final override val camera: Camera
        get() = Game.ui
}