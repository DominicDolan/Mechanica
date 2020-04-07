package demo.polygon

import debug.DebugDrawer
import drawer.Drawer
import game.Game
import geometry.LineSegment
import geometry.PolygonModel
import geometry.isInTriangle
import geometry.triangulation.Triangulator
import geometry.triangulation.TriangulatorList
import gl.renderer.PathRenderer
import input.Keyboard
import input.Mouse
import state.State
import util.colors.hex
import util.extensions.vec
import util.units.Vector

fun main() {
    Game.configure {
        setViewport(height = 1.5)
        setStartingState { StartMain() }
        setResolution(1500, 1500)
        configureDebugMode {
            printWarnings = true
        }
        configureWindow {
            isDecorated = false
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

    val polygonModel = PolygonModel(points)

    val polygonRenderer = PolygonRenderer2()
    init {
        pathRenderer.color = hex(0x404040FF)
        pathRenderer.path = points
        pathRenderer.stroke = 0.004f

    }

    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {
        val mouse = vec(Mouse.worldX, Mouse.worldY)

        polygonRenderer.render(polygonModel)
        pathRenderer.render()

        if (mouse.isInTriangle(points[0], points[1], points[points.size - 2])) {
            draw.yellow
        } else draw.blue
        draw.circle(mouse, 0.05)
    }

}


