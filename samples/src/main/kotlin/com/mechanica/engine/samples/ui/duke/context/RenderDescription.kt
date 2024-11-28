package com.mechanica.engine.samples.ui.duke.context

import com.mechanica.engine.samples.ui.duke.layout.Layout
import com.mechanica.engine.samples.ui.duke.layout.MutableLayout
import com.mechanica.engine.samples.ui.duke.theme.Style

interface RenderDescription {
    val style: Style
    val x: Double
    val y: Double
    val width: Double
    val height: Double
}
class RenderDescriptionImpl(context: DukeContext) {
    val layout: Layout = MutableLayout.create(context)
    val style: Style = Style()

    private val widthScale: Double
    private val heightScale: Double
    private val xAdjust: Double
    private val yAdjust: Double

    val x: Double
        get() = layout.x * widthScale + xAdjust

    val y: Double
        get() = layout.y * heightScale + yAdjust

    val width: Double
        get() = layout.width * widthScale

    val height: Double
        get() = layout.height * heightScale

    init {
        val window = context.window
        val viewport = context.viewport
        widthScale = window.width / viewport.width
        heightScale = window.height / viewport.height
        val signAdjustX = if (widthScale < 0.0) window.width else 0.0
        val signAdjustY = if (heightScale < 0.0) window.height else 0.0
        xAdjust = window.x - widthScale * viewport.x + signAdjustX
        yAdjust = window.y - heightScale * viewport.y + signAdjustY
    }

    override fun toString(): String {
        return "render description: x: $x, y: $y, width: $width, height: $height"
    }
}