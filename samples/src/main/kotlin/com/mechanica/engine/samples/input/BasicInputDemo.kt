package com.mechanica.engine.samples.input

import com.cave.library.angle.degrees
import com.cave.library.angle.plus
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse

fun main() {
    Game.configure {
        setViewport(height = 10.0)
    }

    val draw = Drawer.create()

    val keyboard = Keyboard.create()
    val mouse = Mouse.create()

    var hue = 0.degrees

    Game.loop {
        if (keyboard.space.hasBeenPressed) {
            hue += 30.degrees
        }

        draw.centered.red.hue(hue).circle(mouse.world)
    }
}