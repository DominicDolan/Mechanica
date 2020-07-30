package com.mechanica.engine.game.view

import com.mechanica.engine.unit.vector.Vector

class StaticView(
        override val x: Double = 0.0,
        override val y: Double = 0.0,
        override val width: Double = 1.0,
        override val height: Double = 1.0) : View {

    override val xy: Vector = object : Vector {
        override val x: Double
            get() = this@StaticView.x
        override val y: Double
            get() = this@StaticView.y
    }

    override val wh: Vector = object : Vector {
        override val x: Double
            get() = this@StaticView.width
        override val y: Double
            get() = this@StaticView.height
    }

    override val center: Vector = object : Vector {
        override val x: Double
            get() = this@StaticView.x
        override val y: Double
            get() = this@StaticView.y
    }

    override val ratio: Double
        get() = height/width
}