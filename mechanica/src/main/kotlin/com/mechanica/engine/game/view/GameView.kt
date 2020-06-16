package com.mechanica.engine.game.view

import com.mechanica.engine.game.Game
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.game.configuration.GameSetup
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec

class GameView(data: GameSetup): DynamicView {
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
            get() = this@GameView.x
            set(value) {this@GameView.x = value}
        override var y: Double
            get() = this@GameView.y
            set(value) { this@GameView.y = value}
    }
        set(value) {
            x = value.x
            y = value.y
        }

    @Suppress("SetterBackingFieldAssignment")
    override var center: DynamicVector = object : DynamicVector {
        override var x: Double
            get() = this@GameView.x
            set(value) {this@GameView.x = value}
        override var y: Double
            get() = this@GameView.y
            set(value) { this@GameView.y = value}
    }
        set(value) {
            x = value.x
            y = value.y
        }

    @Suppress("SetterBackingFieldAssignment")
    override var wh: DynamicVector = object : DynamicVector {
        override var x: Double
            get() = this@GameView.width
            set(value) {this@GameView.width = value }
        override var y: Double
            get() = this@GameView.height
            set(value) { this@GameView.height = value}
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