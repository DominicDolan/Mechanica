package com.mechanica.engine.game.view

import com.mechanica.engine.display.Monitor
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.configuration.GameSetup
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec

sealed class Camera : View

class UICamera internal constructor() : Camera() {
    private val scale: Vector

    internal var _width: Double
    override val width: Double
        get() = _width

    internal var _height: Double
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
        get() = Game.world.ratio*(scale.y/scale.x)

    init {
        val contentScale = Monitor.getPrimaryMonitor().contentScale
        scale = vec(contentScale.xScale, contentScale.yScale)
        _width = Game.world.width/scale.x
        _height = Game.world.height/scale.y
    }
}

class WorldCamera internal constructor(data: GameSetup): Camera(), DynamicView {
    private var _width: Double = data.viewWidth
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

    private var _height: Double = data.viewHeight
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

    override var x: Double = data.viewX
        set(value) {
            field = value
            gameMatrices.updateView(this)
        }
    override var y: Double = data.viewY
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

    override var ratio: Double = height/width
        private set
        get() {
            return if (lockRatio) {
                Game.window.aspectRatio
            } else field
        }
    var lockRatio = true

    private val gameMatrices: GameMatrices
        get() = Game.matrices as GameMatrices
}