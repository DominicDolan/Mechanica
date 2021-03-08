package com.mechanica.engine.game.view

import com.cave.library.vector.vec2.VariableVector2

class DefaultDynamicView(
        override var x: Double = 0.0,
        override var y: Double = 0.0,
        override var width: Double = 1.0,
        override var height: Double = 1.0) : DynamicView {

    @Suppress("SetterBackingFieldAssignment")
    override var xy: VariableVector2 = object : VariableVector2 {
        override var x: Double
            get() = this@DefaultDynamicView.x
            set(value) {this@DefaultDynamicView.x = value}
        override var y: Double
            get() = this@DefaultDynamicView.y
            set(value) { this@DefaultDynamicView.y = value}
    }
        set(value) {
            x = value.x
            y = value.y
        }

    @Suppress("SetterBackingFieldAssignment")
    override var wh: VariableVector2 = object : VariableVector2 {
        override var x: Double
            get() = this@DefaultDynamicView.width
            set(value) {this@DefaultDynamicView.width = value }
        override var y: Double
            get() = this@DefaultDynamicView.height
            set(value) { this@DefaultDynamicView.height = value}
    }
        set(value) {
            x = value.x
            y = value.y
        }

    override val ratio: Double
        get() = width/height
}