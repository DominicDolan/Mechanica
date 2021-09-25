package com.mechanica.engine.duke.elements

import com.mechanica.engine.scenes.scenes.SceneHub

abstract class UIComponent : SceneHub() {
    operator fun invoke(element: Element) {
        element.ui()
    }

    abstract fun Element.ui()
}