package demo

import display.Game
import display.GameOptions
import loader.loadTexture
import renderer.Painter
import state.LoadState
import state.State

fun main(args: Array<String>) {

    //Test comment
    val options = GameOptions()
            .setResolution(1280, 720)
            .setViewPort(height = 10.0)
            .setSaveData(Data)
            .setStartingState { StartState() }

    Game.start(options)
    Data.highScore=8
    Game.update()
    Game.destroy()
}

class StartState : State() {
    var position = -8.0
    override fun update(delta: Double) {
        position
        Game.viewHeight = Game.viewHeight + delta
    }

    override fun render(g: Painter) {
        g.color = 0xFF00FFFF
        g.fillCircle(0.0, 0.0, 1.0)
        g.drawCircle(0.0, 2.0, 1.0, 0.1)
    }

}

object Data {
    var highScore = 0
}

//object : State() {
//    var scale = 1.0
//    var translate = 0.0
//
//    override fun update(delta: Float) {
//        scale *= 1.001
//        translate += 0.1
//    }
//
//    override fun render(g: Painter) {
//        g.color = 0xFF00FFFF
//        g.drawRect(0.0, 0.0, 2.0, 2.0)
//        g.color = 0x00FF00FF
//        g.fillCircle(4.0, -1.0, 2.0)
//        g.drawImage(colors, -10.0, 0.0, 3.0, 2.0)
//        g.drawText("Hello World", 1.0, -1.0, 0.0)
//    }
//
//}
