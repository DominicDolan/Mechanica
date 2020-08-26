package com.mechanica.engine.duke.elements

import com.mechanica.engine.scenes.processes.Process

abstract class UIComponent : Process() {
    operator fun invoke(element: Element) {
        element.ui()
    }

    abstract fun Element.ui()
}