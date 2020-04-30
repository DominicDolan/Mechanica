package com.mechanica.engine.samples.shaders

import com.mechanica.engine.game.Game
import com.mechanica.engine.input.Keyboard

fun main() {
    Game.configure {
        setViewport(height = 1.0)
        setResolution(1000, 1000)
        configureWindow {
            isDecorated = false
        }
    }

    Game.view.x = Game.view.width/2f
    Game.view.y = Game.view.height/2f

    val renderer = FragmentRenderer()

    Game.run {
        if (Keyboard.ESC.hasBeenPressed) {
            Game.close()
        }

        renderer.render()
    }
}