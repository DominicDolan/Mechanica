package com.mechanica.engine.game.view

import com.cave.library.vector.vec2.VariableVector2

interface DynamicView : View {
    override var x: Double
    override var y: Double
    override var width: Double
    override var height: Double

    override val xy: VariableVector2
    override val wh: VariableVector2

    companion object {
        fun create(x: Double = 0.0,
                   y: Double = 0.0,
                   width: Double = 1.0,
                   height: Double = 1.0): DynamicView = DefaultDynamicView(x, y, width, height)
    }
}