package demo.shaders

import display.Game
import display.GameOptions
import drawer.Drawer
import state.State

fun main() {
    val options = GameOptions()
            .setResolution(1000, 1000)
//            .setDebugMode(true)
            .setViewPort(height = 1.0)
            .setStartingState { FragmentShaderDemo() }

    Game.start(options)
    Game.viewX = Game.viewWidth/2f
    Game.viewY = Game.viewHeight/2f
    Game.update()
    Game.destroy()
}


private class FragmentShaderDemo : State() {
    val renderer = FragmentRenderer()

    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {
        renderer.render()
    }

}