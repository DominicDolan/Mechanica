package demo.polygon

import drawer.Drawer
import game.Game
import gl.models.Model
import gl.vbo.AttributeArray
import gl.vbo.VBO
import input.Keyboard
import state.State
import util.extensions.vec

fun main() {
    Game.configure {
        setViewport(height = 1.0)
        setStartingState { StartMain() }
        setResolution(1500, 1500)
        configureDebugMode {
            printWarnings = true
        }
        configureWindow {
            isDecorated = false
        }
    }
    Game.view.x = Game.view.width/2f
    Game.view.y = Game.view.height/2f

    Game.run {
        if (Keyboard.ESC.hasBeenPressed) {
            Game.close()
        }
    }
}

private class StartMain : State() {
    val renderer = PolygonRenderer2()

    private val model: Model = Model()

    init {

    }

    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {
        renderer.render(model)
    }

}


