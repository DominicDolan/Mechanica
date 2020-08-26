package com.mechanica.engine.duke.elements

import com.mechanica.engine.duke.context.DukeUIScene
import com.mechanica.engine.duke.defaultDraw

class DefaultElement(scene: DukeUIScene) : Element(scene) {

    override val elementDrawer = ElementDrawer {draw, style ->
        this.defaultDraw(draw, style)
    }
}