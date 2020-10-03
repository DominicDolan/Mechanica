package com.mechanica.engine.duke.elements

import com.mechanica.engine.duke.context.DukeUIScene
import com.mechanica.engine.duke.defaultDraw

class CustomElement(uiScene: DukeUIScene) :  Element(uiScene) {
    override var elementDrawer = ElementDrawer {draw, style ->
        this.defaultDraw(draw, style)
    }
}