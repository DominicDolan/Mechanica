package com.mechanica.engine.samples.ui

import com.mechanica.engine.config.configure
import com.mechanica.engine.game.Game

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setFullscreen(false)
        setStartingScene { BasicDukeUIScene() }
    }

    Game.loop()
}