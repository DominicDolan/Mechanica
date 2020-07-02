package com.mechanica.engine.ui

import com.dubulduke.ui.element.Element
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.Scene

abstract class GUIScene(final override val view: View = Game.ui, order: Int = 10) : Scene(order) {
    val ui = createUI(view)

    override fun render(draw: Drawer) {
        ui(draw) { ui(draw) }
    }

    abstract fun Element<Style, Events>.ui(draw: Drawer)

}