package com.mechanica.engine.samples.geometry

import com.cave.library.color.Color
import com.cave.library.color.hex
import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.config.configure
import com.mechanica.engine.debug.ScreenLog
import com.mechanica.engine.drawer.Drawer
import com.mechanica.engine.drawer.shader.PathRenderer
import com.mechanica.engine.game.Game
import com.mechanica.engine.geometry.lines.LineSegment
import com.mechanica.engine.geometry.lines.LineSegmentImpl
import com.mechanica.engine.input.keyboard.Keyboard
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.scenes.scenes.Scene

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingScene { LinesDemo() }
    }

    Game.loop()
}

class LinesDemo : Scene() {
    private val stroke = 0.05f

    private val renderer = PathRenderer()
    private val l1 = LineSegment(vec(0.0, 0.0), vec(1.0, 0.0))
    private val l2 = LineSegment(vec(0.0, 1.0), vec(1.0, 1.0))

    private val l1Color = hex(0xFFFF00FF)
    private val l2Color = hex(0x00FFFFFF)

    private val pathHolder = ArrayList<Vector2>()

    init {
        renderer.stroke = stroke
        pathHolder.add(l1.p1)
        pathHolder.add(l1.p2)
        renderer.fillFloats(pathHolder)

    }

    override fun update(delta: Double) {
        fun setPoint(point: LineSegmentImpl.LinePoint) {
            point.x = Mouse.world.x
            point.y = Mouse.world.y
        }

        if (Mouse.MB1()) {
            if (Keyboard.shift()) {
                setPoint(l2.p1 as LineSegmentImpl.LinePoint)
            } else {
                setPoint(l1.p1 as LineSegmentImpl.LinePoint)
            }
        }
        if (Mouse.MB2()) {
            if (Keyboard.shift()) {
                setPoint(l2.p2 as LineSegmentImpl.LinePoint)
            } else {
                setPoint(l1.p2 as LineSegmentImpl.LinePoint)
            }
        }
    }

    override fun render(draw: Drawer) {
        val v = Game.world

        draw.darkGrey.centered.rectangle(0, 0, v.width, stroke)
        draw.darkGrey.centered.rectangle(0, 0, stroke, v.height)

        drawIntercepts(draw, l1, l1Color)
        drawIntercepts(draw, l2, l2Color)

        if (l1.segmentIntersect(l2)) {
            draw.magenta
        } else draw.red
        draw.circle(l1.intersect(l2), 0.1)

        draw.renderLine(l1, l1Color)
        draw.renderLine(l2, l2Color)

        ScreenLog { "Line1: $l1" }
        ScreenLog { "Line2: $l2" }

    }

    private fun drawIntercepts(draw:  Drawer, line: LineSegment, color: Color) {
        draw.centered.color(color).circle(0, line.b, 0.1)
        draw.centered.color(color).circle(line.c, 0, 0.1)
    }

    private fun Drawer.renderLine(line: LineSegment, color: Color) {
        this.color.strokeColor(color, strokeWidth = 0.1).line(line.p1, line.p2)
    }

    private fun setPath(line: LineSegment) {
        pathHolder[0] = line.p1
        pathHolder[1] = line.p2
    }
}