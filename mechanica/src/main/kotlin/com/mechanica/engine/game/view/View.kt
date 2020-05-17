package com.mechanica.engine.game.view

import com.mechanica.engine.unit.vector.Vector

interface View {
    val width: Double
    val height: Double
    val x: Double
    val y: Double

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
}