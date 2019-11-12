package demo.text

import display.Game
import display.GameOptions
import graphics.drawer.Drawer
import loader.loadImageFromResource
import loader.loadTexture
import resources.Res
import state.State

fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
            .setViewPort(height = 10.0)
            .setStartingState { StartText() }

    Game.start(options)
    Game.update()
    Game.destroy()
}


private class StartText : State() {
    val image = loadImageFromResource(Res.image["colors"])
    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {
        draw.red.rectangle(0, 0, 1.0, 1.0)
        draw.magenta.text("Hi", 1.0, 0.0, 1.0)
    }

}

/*
72
Translated points: [-0.054545455, 1.1454545, -0.054545455, 0.07272727, 0.8363636, 0.07272727, 0.8363636, 0.07272727, 0.8363636, 1.1454545, -0.054545455, 1.1454545]
105
Translated points: [0.6727273, 1.1454545, 0.6727273, 0.07272727, 1.0909091, 0.07272727, 1.0909091, 0.07272727, 1.0909091, 1.1454545, 0.6727273, 1.1454545]
 */
