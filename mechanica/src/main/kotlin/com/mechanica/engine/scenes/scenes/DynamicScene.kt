package com.mechanica.engine.scenes.scenes

import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.game.view.DynamicView
import com.mechanica.engine.game.view.View
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.Vector

abstract class DynamicScene(
        x: Double = 0.0,
        y: Double = 0.0,
        width: Double = 1.0,
        height: Double = 1.0,
        priority: Int = 0) : Scene(priority) {

    constructor(
            view: View,
            priority: Int = 0) : this(view.x, view.y, view.width, view.height, priority)

    open val position = DynamicVector.create(x + width/2.0, y + height/2.0)

    override val view: DynamicView = DynamicSceneView(width, height)

    override fun drawInScene(draw: Drawer, view: View): Drawer {
        return draw.centered.transformed.translate(position).scale(view.wh)
    }

    inner class DynamicSceneView(
            override var width: Double,
            override var height: Double) : DynamicView {

        override var x: Double
            get() = center.x - width/2.0
            set(value) { center.x = value + width/2.0}
        override var y: Double
            get() = center.y - height/2.0
            set(value) {center.y = value + height/2.0}

        @Suppress("SetterBackingFieldAssignment")
        override var center: DynamicVector = object : DynamicVector by position {}
            set(value) {
                position.x = value.x
                position.y = value.y
            }

        @Suppress("SetterBackingFieldAssignment")
        override var xy: DynamicVector = object : DynamicVector {
            override var x: Double
                get() = this@DynamicSceneView.width
                set(value) {this@DynamicSceneView.width = value }
            override var y: Double
                get() = this@DynamicSceneView.height
                set(value) { this@DynamicSceneView.height = value}
        }
            set(value) {
                x = value.x
                y = value.y
            }

        @Suppress("SetterBackingFieldAssignment")
        override var wh: DynamicVector = object : DynamicVector {
            override var x: Double
                get() = this@DynamicSceneView.width
                set(value) {this@DynamicSceneView.width = value }
            override var y: Double
                get() = this@DynamicSceneView.height
                set(value) { this@DynamicSceneView.height = value}
        }
            set(value) {
                width = value.x
                height = value.y
            }
    }
}