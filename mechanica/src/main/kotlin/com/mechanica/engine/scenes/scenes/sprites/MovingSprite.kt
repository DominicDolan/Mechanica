package com.mechanica.engine.scenes.scenes.sprites

import com.cave.library.vector.vec2.VariableVector2
import com.cave.library.vector.vec2.Vector2
import com.mechanica.engine.game.view.MovingViewable
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.Scene

abstract class MovingSprite(x: Double = 0.0,
                            y: Double = 0.0,
                            width: Double = 1.0,
                            height: Double = 1.0,
                            order: Int = 0
) : Scene(order), MovingViewable {

    override val position = VariableVector2.create(x + width/2.0, y + height/2.0)
    override val view: View by lazy { MovingView(width, height) }

    inner class MovingView(
            override val width: Double,
            override val height: Double) : View {

        override val x: Double
            get() = position.x
        override val y: Double
            get() = position.y

        override val xy: VariableVector2
            get() = position

        override var wh: Vector2 = object : Vector2 {
            override val x: Double
                get() = this@MovingView.width
            override val y: Double
                get() = this@MovingView.height
        }
    }
}