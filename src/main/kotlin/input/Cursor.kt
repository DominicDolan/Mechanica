package input

import display.Game

object Cursor {
    var x: Double = 0.0
        internal set(value) {
            field = value
            val ratio = Game.width/Game.viewWidth
            viewX = value/ratio - Game.viewWidth/2.0
            worldX = viewX + Game.viewX
        }

    var y: Double = 0.0
        internal set(value) {
            field = value
            val ratio = Game.height/Game.viewHeight
            viewY = Game.viewHeight/2.0 - value/ratio
            worldY = viewY + Game.viewY

        }

    var viewX: Double = 0.0; internal set
    var viewY: Double = 0.0; internal set

    var worldX: Double = 0.0; internal set
    var worldY: Double = 0.0; internal set
}