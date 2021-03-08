package com.mechanica.engine.game.view

import com.cave.library.vector.vec2.Vector2

class StaticView(
        override val x: Double = 0.0,
        override val y: Double = 0.0,
        override val width: Double = 1.0,
        override val height: Double = 1.0) : View {

    override val xy: Vector2 = object : Vector2 {
        override val x: Double
            get() = this@StaticView.x
        override val y: Double
            get() = this@StaticView.y
    }

    override val wh: Vector2 = object : Vector2 {
        override val x: Double
            get() = this@StaticView.width
        override val y: Double
            get() = this@StaticView.height
    }

    override val ratio: Double
        get() = width/height
}