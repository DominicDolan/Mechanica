package input

import game.Game

object Cursor {
    var x: Double = 0.0
        internal set(value) {
            field = value
            val ratio = Game.window.width.toDouble()/Game.view.width
            viewX = value/ratio - Game.view.width/2.0
            worldX = viewX + Game.view.x
        }

    var y: Double = 0.0
        internal set(value) {
            field = value
            val ratio = Game.window.height.toDouble()/Game.view.height
            viewY = Game.view.height/2.0 - value/ratio
            worldY = viewY + Game.view.y

        }

    var viewX: Double = 0.0; internal set
    var viewY: Double = 0.0; internal set

    var worldX: Double = 0.0; internal set
    var worldY: Double = 0.0; internal set
}