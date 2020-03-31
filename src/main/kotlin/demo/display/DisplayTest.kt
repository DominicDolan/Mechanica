package demo.display

import drawer.Drawer
import game.Game2
import state.State

fun main() {
    Game2.configure {
        setViewport(height = 10.0)
        setStartingState { DisplayTestState() }
    }

    Game2.run()
}

class DisplayTestState : State() {
    override fun update(delta: Double) { }

    override fun render(draw: Drawer) {
        draw.yellow.background()
        draw.green.rectangle(-1.0, 0, 1, 1)
        draw.blue.circle(0, 3, 0.5)
        draw.magenta.text("Hello, mechanica", 1.0, 1.0, 0)

        draw.centered.red.rectangle(-3, 0, 1, 9.9)
    }
}