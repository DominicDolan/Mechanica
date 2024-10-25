package com.mechanica.engine.duke.elements

import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.scenes.scenes.SceneHub

abstract class UIComponent : SceneHub() {
    protected val mouse = Mouse.create()

    protected inline fun Element.onClick(onClick: () -> Unit) {
        if (isHovering && mouse.MB1.hasBeenPressed) {
            onClick()
        }
    }

    operator fun invoke(element: Element) {
        element.ui()
    }

    abstract fun Element.ui()
}