package demo.shaders

import game.Game
import input.Keyboard

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