package com.mechanica.engine.game.view

import com.mechanica.engine.display.Monitor
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.configuration.GameSetup
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec

sealed class GameView : View

class UIView internal constructor() : GameView() {
    private val scale: Vector
    override val width: Double
    override val height: Double
    override val x: Double = 0.0
    override val y: Double = 0.0
    override val xy: Vector = vec(0.0, 0.0)
    override val wh: Vector
    override val center: Vector
    override val ratio: Double
        get() = Game.view.ratio*(scale.y/scale.x)

    init {
        val contentScale = Monitor.getPrimaryMonitor().contentScale
        scale = vec(contentScale.xScale, contentScale.yScale)
        width = Game.view.width/scale.x
        height = Game.view.height/scale.y
        wh = vec(width, height)
        center = vec(0.0, 0.0)
    }
}

class WorldView internal constructor(data: GameSetup): GameView(), DynamicView {
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
            get() = this@WorldView.x
            set(value) {this@WorldView.x = value}
        override var y: Double
            get() = this@WorldView.y
            set(value) { this@WorldView.y = value}
    }
        set(value) {
            x = value.x
            y = value.y
        }

    @Suppress("SetterBackingFieldAssignment")
    override var center: DynamicVector = object : DynamicVector {
        override var x: Double
            get() = this@WorldView.x
            set(value) {this@WorldView.x = value}
        override var y: Double
            get() = this@WorldView.y
            set(value) { this@WorldView.y = value}
    }
        set(value) {
            x = value.x
            y = value.y
        }

    @Suppress("SetterBackingFieldAssignment")
    override var wh: DynamicVector = object : DynamicVector {
        override var x: Double
            get() = this@WorldView.width
            set(value) {this@WorldView.width = value }
        override var y: Double
            get() = this@WorldView.height
            set(value) { this@WorldView.height = value}
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