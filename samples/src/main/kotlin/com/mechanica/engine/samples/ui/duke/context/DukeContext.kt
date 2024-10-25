package com.mechanica.engine.samples.ui.duke.context

abstract class DukeContext {

    abstract val window: DukeWindow
    abstract val viewport: DukeViewport

    val leftIsLow by lazy {
        val horizontalIsRightwards = window.right > window.left
        val viewportWidthIsPositive = viewport.width >= 0
        !(horizontalIsRightwards xor viewportWidthIsPositive)
    }

    val bottomIsLow by lazy {
        val verticalIsUpwards = window.top > window.bottom
        val viewportHeightIsPositive = viewport.height >= 0
        !(verticalIsUpwards xor viewportHeightIsPositive)
    }

    abstract fun renderElement(renderContext: RenderDescription)
}