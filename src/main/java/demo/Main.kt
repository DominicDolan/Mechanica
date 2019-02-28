package demo

import display.Game
import display.GameBuilder
import loader.loadTexture
import renderer.Painter
import state.LoadState
import state.State

fun main(args: Array<String>) {

    var colors = 0;
    GameBuilder()
            .setResolution(1280, 720)
            .setLoader(object : LoadState() {
                override fun preLoad() {

                }

                override fun renderLoadScreen(g: Painter) {
                    g.color = 0xFF00FFFF
                    g.drawRect(0.0, 0.0, 100.0, 100.0)
                }

                override fun load() {
                    colors = loadTexture("images/colors.png")
                    Thread.sleep(1000)
                }
            })
            .setStartingState(object : State() {
                var scale = 1.0
                var translate = 0.0
                override fun init() {

                }

                override fun update(delta: Float) {
                    scale *= 1.001
                    translate += 0.1
                }

                override fun render(g: Painter) {
                    g.color = 0xFF00FFFF
                    g.drawRect(0.0, 0.0, 200.0, 200.0)
                    g.color = 0x00FF00FF
                    g.fillCircle(50.0, -100.0, 20.0)
                    g.drawImage(colors, 0.0, 0.0, 300.0, 200.0)
                    g.drawText("Hello World", 100.0, -64.0, 0.0)
                }

            })
            .start()
    Game.update()
    Game.destroy()
}
