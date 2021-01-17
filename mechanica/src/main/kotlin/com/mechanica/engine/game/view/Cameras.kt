package com.mechanica.engine.game.view

import com.mechanica.engine.game.Game
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec

sealed class Camera : View

class UICamera internal constructor(
        private val matrices: GameMatrices,
        private val world: WorldCamera,
        private val scale: Vector) : Camera() {

    private var _width: Double = world.width/scale.x
    override val width: Double
        get() = _width

    private var _height: Double = world.height/scale.y
    override val height: Double
        get() = _height

    override val x: Double = 0.0
    override val y: Double = 0.0
    override val xy: Vector = vec(0.0, 0.0)
    override val wh: Vector = object : Vector {
        override val x: Double
            get() = width
        override val y: Double
            get() = height

    }
    override val ratio: Double
        get() = world.ratio*(scale.x/scale.y)

    fun update(width: Double, height: Double) {
        _width = width
        _height = height
        matrices.setUiView(height)
    }
}

class WorldCamera internal constructor(view: View, private val gameMatrices: GameMatrices): Camera(), DynamicView {
    private var _width: Double = view.width
    override var width: Double
        get() = _width
        set(value) {
            _width = value
            if (lockRatio) {
                _height = value/ratio
            } else {
                ratio = value/_height
            }
            gameMatrices.updateView(this)
        }

    private var _height: Double = view.height
    override var height: Double
        get() = _height
        set(value) {
            _height = value
            if (lockRatio) {
                _width = value*ratio
            } else {
                ratio = _width/value
            }
            gameMatrices.updateView(this)
        }

    override var x: Double = view.x
        set(value) {
            field = value
            gameMatrices.updateView(this)
        }
    override var y: Double = view.y
        set(value) {
            field = value
            gameMatrices.updateView(this)
        }

    @Suppress("SetterBackingFieldAssignment")
    override var xy: DynamicVector = object : DynamicVector {
        override var x: Double
            get() = this@WorldCamera.x
            set(value) {this@WorldCamera.x = value}
        override var y: Double
            get() = this@WorldCamera.y
            set(value) { this@WorldCamera.y = value}
    }
        set(value) {
            x = value.x
            y = value.y
        }

    @Suppress("SetterBackingFieldAssignment")
    override var wh: DynamicVector = object : DynamicVector {
        override var x: Double
            get() = this@WorldCamera.width
            set(value) {this@WorldCamera.width = value }
        override var y: Double
            get() = this@WorldCamera.height
            set(value) { this@WorldCamera.height = value}
    }
        set(value) {
            x = value.x
            y = value.y
        }

    override var ratio: Double = width/height
        private set
        get() {
            return if (lockRatio) {
                Game.surface.resolution.aspectRatio
            } else field
        }
    var lockRatio = true
}