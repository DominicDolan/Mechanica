package demo.polygon

import display.Game
import display.GameOptions
import drawer.Drawer
import gl.utils.createUnitSquareArray
import gl.vbo.AttributeArray
import gl.vbo.pointer.VBOPointer
import gl.models.Model
import state.State
import util.extensions.toFloatArray

fun main() {
    val options = GameOptions()
            .setResolution(1000, 1000)
//            .setDebugMode(true)
            .setViewPort(height = 1.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.viewX = Game.viewWidth/2f
    Game.viewY = Game.viewHeight/2f
    Game.update()
    Game.destroy()
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


