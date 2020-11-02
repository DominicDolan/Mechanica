package com.mechanica.engine.samples.basic

import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setFullscreen(false)
    }

    val draw = Drawer.create()

    Game.loop {
        draw.centered.grey.text("Hello, Mechanica")
    }
}