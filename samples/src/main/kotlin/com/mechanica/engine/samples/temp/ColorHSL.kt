package com.mechanica.engine.samples.temp

import com.cave.library.color.rgba
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.mouse.Mouse

fun main() {
    Game.configure {
        setViewport(height = 10.0)
    }
    val draw = Drawer.create()

    Game.loop {
        val color = rgba(0.5, 0.5, 0.5, 1.0)
        draw.centered.cyan.rectangle(x = 2.5, width = 5.0, height = 5.0)
        draw.centered.color(color).lightness(Mouse.normalized.x).saturation(Mouse.normalized.y).rectangle(width = 5.0, height = 5.0)
    }
}