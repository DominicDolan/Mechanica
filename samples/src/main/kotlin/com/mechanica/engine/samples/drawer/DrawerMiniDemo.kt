package com.mechanica.engine.samples.drawer

import com.cave.library.angle.degrees
import com.cave.library.angle.plus
import com.cave.library.matrix.mat4.Matrix4
import com.mechanica.engine.config.configure
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game

fun main() {
    Game.configure {
        this.setViewport(height = 10.0)
        this.setFullscreen(false)
    }

    val draw = Drawer.create()
    val matrix = Matrix4.identity()

    var movingAngle = 0.0.degrees

    Game.loop {
        matrix.identity()
            .translation.apply(-2.0, 0.0, 0.0)
            .rotation.apply(movingAngle)
            .scale.apply(1.0, 2.0, 0.0)

        draw.green.transformed.matrix(matrix).rectangle()

        movingAngle += 0.5.degrees
    }
}