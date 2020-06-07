package com.mechanica.engine.game.view

import com.mechanica.engine.unit.vector.DynamicVector

class DefaultDynamicView(
        override var x: Double = 0.0,
        override var y: Double = 0.0,
        override var width: Double = 1.0,
        override var height: Double = 1.0) : DynamicView {

    @Suppress("SetterBackingFieldAssignment")
    override var xy: DynamicVector = object : DynamicVector {
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
    override var wh: DynamicVector = object : DynamicVector {
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

    @Suppress("SetterBackingFieldAssignment")
    override var center: DynamicVector = object : DynamicVector {
        override var x: Double
            get() = this@DefaultDynamicView.x + width/2.0
            set(value) {this@DefaultDynamicView.x = value - width/2.0}
        override var y: Double
            get() = this@DefaultDynamicView.y + height/2.0
            set(value) { this@DefaultDynamicView.y = value - height/2.0}
    }
        set(value) {
            x = value.x
            y = value.y
        }

    override val ratio: Double
        get() = height/width
}