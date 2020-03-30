package game.view

import game.Game2
import game.configuration.GameSetup
import util.units.MutableVector
import util.units.Vector

class GameView(data: GameSetup) {
    private var _width: Double = data.viewWidth
    var width: Double
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
    var height: Double
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

    var x: Double = data.viewX
        set(value) {
            field = value
            gameMatrices.updateView(this)
        }
    var y: Double = data.viewY
        set(value) {
            field = value
            gameMatrices.updateView(this)
        }

    var ratio: Double = height/width
        private set
        get() {
            return if (lockRatio) {
                Game2.window.aspectRatio
            } else field
        }
    var lockRatio = true

    val bottom: Double
        get() = y
    val top: Double
        get() = y + height
    val left: Double
        get() = x
    val right: Double
        get() = x + width

    val center: Vector = MutableVector(x + width/2.0, y + height/2.0)
        get() {
            val vector = field as MutableVector
            vector.x = x + width/2.0
            vector.y = y + height/2.0
            return vector
        }

    private val gameMatrices: GameMatrices
        get() = Game2.matrices as GameMatrices
}