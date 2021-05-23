package com.mechanica.engine.samples.drawer

import com.cave.library.angle.degrees
import com.cave.library.angle.plus
import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.matrix.mat4.applyRotation
import com.cave.library.matrix.mat4.applyScale
import com.cave.library.matrix.mat4.applyTranslation
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

    println(matrix)
    var angle = 0.0.degrees

    Game.loop {
        matrix
            .applyTranslation(-2.0, 0.0, 0.0)
            .applyRotation(angle)
            .applyScale(1.0, 2.0, 0.0)
//        draw.red.rotated(angle).rectangle(0.0, 0.0, 1.0, 2.0)
        draw.green.transformed.matrix(matrix).rectangle()

        angle += 0.5.degrees
    }
}