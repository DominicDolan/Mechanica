package com.mechanica.engine.ui

import com.dubulduke.ui.UIContext
import com.dubulduke.ui.Viewport
import com.dubulduke.ui.Window
import com.dubulduke.ui.element.ElementData
import com.dubulduke.ui.render.RenderDescription
import com.mechanica.engine.game.view.View
import com.mechanica.engine.ui.style.Style

fun createUI(view: View): MechanicaUI {

    val window = Window(-view.width/2.0, -view.height/2.0, view.width, view.height)
    val viewport = Viewport(0.0, 0.0, view.width, -view.height)
    val options = UIContext(window, viewport, GameElementData())

    return MechanicaUI(options)
}


class GameElementData : ElementData<Style, Events>() {
    override fun createEvent(description: RenderDescription<Style>) = Events(description)

    override fun createStyle() = Style()
}
