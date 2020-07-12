package com.mechanica.engine.samples.polygon

import com.mechanica.engine.color.hex
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.shader.PathRenderer
import com.mechanica.engine.game.Game
import com.mechanica.engine.geometry.lines.LineSegment
import com.mechanica.engine.geometry.lines.LineSegmentImpl
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.models.PolygonModel
import com.mechanica.engine.scenes.scenes.WorldScene
import com.mechanica.engine.unit.vector.vec

fun main() {
    Game.configure {
        setViewport(height = 1.5)
        setStartingScene { StartMain() }
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
        if (Keyboard.esc.hasBeenPressed) {
            Game.close()
        }
    }
}

private class StartMain : WorldScene() {
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

    val polygonModel = PolygonModel(points)

    val polygonRenderer = PolygonRenderer()
    init {
        pathRenderer.color = hex(0x404040FF)
        pathRenderer.fillFloats(points)
        pathRenderer.stroke = 0.004f

    }

    override fun update(delta: Double) {
        val line = this.line as LineSegmentImpl
        line.p2.x = Mouse.world.x
        line.p2.y = Mouse.world.y
    }

    override fun render(draw: Drawer) {
        polygonRenderer.render(polygonModel)

    }

}


