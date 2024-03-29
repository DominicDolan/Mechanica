package com.mechanica.engine.scenes.scenes.sprites

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.view.View
import com.mechanica.engine.game.view.Viewable
import com.mechanica.engine.scenes.scenes.Scene

abstract class StaticSprite(override val view: View) : Scene(), Viewable {
    constructor(
            x: Double = 0.0,
            y: Double = 0.0,
            width: Double = 1.0,
            height: Double = 1.0) : this(View.create(x, y, width, height))

    abstract override fun render(draw: Drawer)
}