package com.mechanica.engine.game.view

import com.cave.library.vector.vec2.Vector2

interface View {
    val width: Double
    val height: Double
    val x: Double
    val y: Double
    val xy: Vector2
    val wh: Vector2

    val bottom: Double
        get() = y - height/2.0
    val top: Double
        get() = y + height/2.0
    val left: Double
        get() = x - width/2.0
    val right: Double
        get() = x + width/2.0

    val ratio: Double
        get() = width/height

    companion object {
        fun create(x: Double = 0.0,
                   y: Double = 0.0,
                   width: Double = 1.0,
                   height: Double = 1.0): View = StaticView(x, y, width, height)
    }
}