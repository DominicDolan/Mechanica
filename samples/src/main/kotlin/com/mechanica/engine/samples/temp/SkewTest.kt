package com.mechanica.engine.samples.temp

import com.cave.library.angle.degrees
import com.cave.library.angle.radians
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.mouse.Mouse

fun main() {
    Game.configure {
        setViewport(height = 10.0)
    }

    val draw = Drawer.create()

    var skewY = Mouse.ui.theta
    var skewX = 0.radians

    Game.loop {
        if (Mouse.MB1()) {
            skewX = Mouse.ui.theta
        } else {
            skewY = Mouse.ui.theta
        }
        draw.lightGrey.centered.rotated(10.degrees).rectangle(2, 2, width = 2.0, height = 2.0)
        draw.darkGrey.transformed.skew(skewX, skewY).rotated(10.degrees).rectangle(2,2, 1, 1)

    }
}