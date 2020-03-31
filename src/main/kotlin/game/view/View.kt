package game.view

import util.units.Vector

interface View {
    val width: Double
    val height: Double
    val x: Double
    val y: Double

    val bottom: Double
        get() = y
    val top: Double
        get() = y + height
    val left: Double
        get() = x
    val right: Double
        get() = x + width

    val center: Vector

    val ratio: Double
}