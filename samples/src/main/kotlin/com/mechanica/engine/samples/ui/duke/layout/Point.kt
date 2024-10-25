package com.mechanica.engine.samples.ui.duke.layout

interface Point {
    val x: Double
    val y: Double
}

interface MutablePoint : Point {
    override var x: Double
    override var y: Double

    fun set(other: Point) {
        this.x = other.x
        this.y = other.y
    }
}