package com.mechanica.engine.game.view

import com.mechanica.engine.unit.vector.DynamicVector

interface DynamicView : View {
    override var x: Double
    override var y: Double
    override var width: Double
    override var height: Double

    override var xy: DynamicVector
    override var wh: DynamicVector
    override var center: DynamicVector

    companion object {
        fun create(x: Double = 0.0,
                   y: Double = 0.0,
                   width: Double = 1.0,
                   height: Double = 1.0): DynamicView = DefaultDynamicView(x, y, width, height)
    }
}