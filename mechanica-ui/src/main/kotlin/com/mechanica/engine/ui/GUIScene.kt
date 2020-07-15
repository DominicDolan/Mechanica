package com.mechanica.engine.ui

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.View
import com.mechanica.engine.scenes.scenes.Scene
import com.mechanica.engine.ui.elements.DrawerElement

abstract class GUIScene(final override val view: View = Game.ui, order: Int = 10) : Scene(order) {
    val ui = createUI(view)

    override fun render(draw: Drawer) {
        ui(draw) { ui(draw) }
    }

    abstract fun DrawerElement.ui(draw: Drawer)

}