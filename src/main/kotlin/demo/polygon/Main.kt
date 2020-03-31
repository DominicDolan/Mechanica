package demo.polygon

import drawer.Drawer
import game.Game
import gl.models.Model
import gl.utils.createUnitSquareArray
import gl.vbo.AttributeArray
import gl.vbo.pointer.VBOPointer
import state.State
import util.extensions.toFloatArray

fun main() {
    Game.configure {
        setViewport(height = 1.0)
        setStartingState { StartMain() }
    }
    Game.view.x = Game.view.width/2f
    Game.view.y = Game.view.height/2f

    Game.run()
}

private class StartMain : State() {
    val renderer = PolygonRenderer()

    private val vbo = AttributeArray(createUnitSquareArray().toFloatArray(3), VBOPointer.position)

    private val model = Model(vbo)

    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {
        renderer.render(model)
    }

}


