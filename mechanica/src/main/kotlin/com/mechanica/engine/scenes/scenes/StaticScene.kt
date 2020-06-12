package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.view.View
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec

abstract class StaticScene(final override val view: View, order: Int = 0) : Scene(order) {
    constructor(
            x: Double = 0.0,
            y: Double = 0.0,
            width: Double = 1.0,
            height: Double = 1.0,
            priority: Int = 0) : this(View.create(x, y, width, height), priority)

    val position: Vector by lazy { vec(view.xy) }

    override fun drawInScene(draw: Drawer, view: View): Drawer {
        return draw.centered.transformed.translate(position).scale(view.wh)
    }
}