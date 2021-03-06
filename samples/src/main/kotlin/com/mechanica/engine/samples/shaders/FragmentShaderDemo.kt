package com.mechanica.engine.samples.shaders

import com.mechanica.engine.config.configure
import com.mechanica.engine.display.DesktopWindow
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.keyboard.Keyboard

fun main() {
    Game.configure {
        setViewport(height = 1.0)
        setResolution(1000, 1000)
        configureDrawSurface {
            (this as? DesktopWindow)?.isDecorated = false
        }
    }

    Game.world.x = Game.world.width/2f
    Game.world.y = Game.world.height/2f

    val renderer = FragmentRenderer()

    Game.loop {
        if (Keyboard.esc.hasBeenPressed) {
            Game.close()
        }

        renderer.render()
    }
}