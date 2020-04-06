package demo.polygon

import debug.DebugDrawer
import drawer.Drawer
import game.Game
import geometry.LineSegment
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
    val polygonRenderer = PolygonRenderer2()
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

    val triangulatorList = TriangulatorList(points.toTypedArray())
    val triangulator = Triangulator(triangulatorList)
    init {
        pathRenderer.color = hex(0x404040FF)
        pathRenderer.path = points
        pathRenderer.stroke = 0.004f

        triangulator.triangulate()

//        polygonRenderer.fillShorts(triangulator)
    }

    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {
        val v = Game.view
        val mouse = vec(Mouse.worldX, Mouse.worldY)

        polygonRenderer.render(triangulator)
//        pathRenderer.render()
        for (n in triangulatorList) {
            if (!n.isConcave) {
                draw.magenta.circle(n, 0.04)
            }
        }

        for (n in triangulatorList.concaveList) {
            draw.yellow.circle(n, 0.04)
        }

        drawDiagonals(draw)

        if (mouse.isInTriangle(points[0], points[1], points[points.size - 2])) {
            draw.yellow
        } else draw.blue
        draw.circle(mouse, 0.05)
        DebugDrawer.drawText("CCW: ${triangulatorList.ccw}")
    }

    private fun drawDiagonals(draw: Drawer) {
        val lines = triangulator.lines
        if (Keyboard.N1()){
            drawLine(draw, lines[0])
        }
        if (Keyboard.N2()){
            drawLine(draw, lines[1])
        }
        if (Keyboard.N3()){
            drawLine(draw, lines[2])
        }
        if (Keyboard.N4()){
            drawLine(draw, lines[3])
        }
        if (Keyboard.N5()){
            drawLine(draw, lines[4])
        }
        if (Keyboard.N6()){
            drawLine(draw, lines[5])
        }
        if (Keyboard.N7()){
            drawLine(draw, lines[6])
        }
        if (Keyboard.N8()){
            drawLine(draw, lines[7])
        }
        if (Keyboard.N9()){
            drawLine(draw, lines[8])
        }
    }

    private fun drawLine(draw: Drawer, line: LineSegment) {
        draw.stroke(0.006).lightGrey.line(line.p1, line.p2)
    }
    private fun drawLine(draw: Drawer, p1: Vector, p2: Vector) {
        draw.stroke(0.006).lightGrey.line(p1, p2)
    }
}


