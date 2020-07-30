package com.mechanica.engine.game.view

import com.mechanica.engine.unit.vector.Vector

interface View {
    val width: Double
    val height: Double
    val x: Double
    val y: Double
    val xy: Vector
    val wh: Vector

    val bottom: Double
        get() = y - height/2.0
    val top: Double
        get() = y + height/2.0
    val left: Double
        get() = x - width/2.0
    val right: Double
        get() = x + width/2.0

    val center: Vector

    val ratio: Double
        get() = width/height

    companion object {
        fun create(x: Double = 0.0,
                   y: Double = 0.0,
                   width: Double = 1.0,
                   height: Double = 1.0): View = StaticView(x, y, width, height)
    }
}