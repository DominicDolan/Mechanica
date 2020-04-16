package demo.polygon

import drawer.Drawer
import game.Game
import gl.models.DynamicPolygonModel
import geometry.lines.LineSegment
import geometry.lines.LineSegmentImpl
import gl.renderer.PathRenderer
import input.Keyboard
import input.Mouse
import state.State
import util.colors.hex
import util.extensions.vec

fun main() {
    Game.configure {
        setViewport(height = 1.5)
        setStartingState { StartMain() }
        setResolution(1500, 1500)
        configureDebugMode {
            printWarnings = true
        }
        configureWindow {
//            isDecorated = false
        }
    }
    Game.view.x = 0.5
    Game.view.y = 0.5

    Game.run {
        if (Keyboard.ESC.hasBeenPressed) {
            Game.close()
        }
    }
}

private class StartMain : State() {
    val pathRenderer = PathRenderer()

    val points = arrayOf(
            vec(0, 0),
            vec(0.5, 0.1),
            vec(1.0, 0.0),
            vec(0.5, 0.5),
            vec(1.0, 0.7),
            vec(1.0, 1.0),
            vec(0.7, 1.0),
            vec(0.7, 0.65),
            vec(0.0, 1.0),
            vec(0.2, 0.5),
            vec(0.0, 0.0)
    ).toList()

    val line = LineSegment(vec(0.01, 0.01), vec(0.02, 0.02))

    val polygonModel = DynamicPolygonModel(points)

    val polygonRenderer = PolygonRenderer2()
    init {
        pathRenderer.color = hex(0x404040FF)
        pathRenderer.path = points
        pathRenderer.stroke = 0.004f

    }

    override fun update(delta: Double) {
        val line = this.line as LineSegmentImpl
        line.p2.x = Mouse.worldX
        line.p2.y = Mouse.worldY

        if (Mouse.MB1.hasBeenPressed) {
            polygonModel.add(vec(Mouse.worldX, Mouse.worldY), 3)
        }
    }

    override fun render(draw: Drawer) {
        val mouse = vec(Mouse.worldX, Mouse.worldY)

        polygonRenderer.render(polygonModel)

    }

}


