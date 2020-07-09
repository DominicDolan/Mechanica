package com.mechanica.engine.samples.basic

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game

fun main() {
    Game.configure {
        setViewport(height = 10.0)
    }

    val draw = Drawer.create()

    Game.run {
        draw.centered.grey.text("Hello, Mechanica")
    }
}