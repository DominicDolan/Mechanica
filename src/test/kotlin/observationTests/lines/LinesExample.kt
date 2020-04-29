package observationTests.lines

import debug.DebugDrawer
import debug.ScreenLog
import drawer.Drawer
import drawer.shader.PathRenderer
import game.Game
import geometry.lines.LineSegment
import geometry.lines.LineSegmentImpl
import input.Keyboard
import input.Mouse
import org.joml.Matrix4f
import state.State
import util.colors.Color
import util.colors.hex
import util.extensions.vec
import util.units.Vector

class LinesExample : State() {
    private val stroke = 0.05f

    private val renderer = PathRenderer()
    private val l1 = LineSegment(vec(0.0, 0.0), vec(1.0, 0.0))
    private val l2 = LineSegment(vec(0.0, 1.0), vec(1.0, 1.0))

    private val l1Color = hex(0xFFFF00FF)
    private val l2Color = hex(0x00FFFFFF)

    private val pathHolder = ArrayList<Vector>()

    private val transformation = Matrix4f().identity()

    init {
        renderer.stroke = stroke
        pathHolder.add(l1.p1)
        pathHolder.add(l1.p2)
        renderer.fillFloats(pathHolder)

    }

    override fun update(delta: Double) {
        fun setPoint(point: LineSegmentImpl.LinePoint) {
            point.x = Mouse.view.x
            point.y = Mouse.view.y
        }

        if (Mouse.MB1()) {
            if (Keyboard.SHIFT()) {
                setPoint(l2.p1 as LineSegmentImpl.LinePoint)
            } else {
                setPoint(l1.p1 as LineSegmentImpl.LinePoint)
            }
        }
        if (Mouse.MB2()) {
            if (Keyboard.SHIFT()) {
                setPoint(l2.p2 as LineSegmentImpl.LinePoint)
            } else {
                setPoint(l1.p2 as LineSegmentImpl.LinePoint)
            }
        }
    }

    override fun render(draw: Drawer) {
        val v = Game.view

        draw.darkGrey.centered.rectangle(0, 0, v.width, stroke)
        draw.darkGrey.centered.rectangle(0, 0, stroke, v.height)

        drawIntercepts(draw, l1, l1Color)
        drawIntercepts(draw, l2, l2Color)

        if (l1.segmentIntersect(l2)) {
            draw.magenta
        } else draw.red
        draw.circle(l1.intersect(l2), 0.1)

        renderLine(l1, l1Color)
        renderLine(l2, l2Color)

//        ScreenLog { "Line1: $l1" }
//        ScreenLog { "Line2: $l2" }

    }

    private fun drawIntercepts(draw:  Drawer, line: LineSegment, color: Color) {
        draw.centered.color(color).circle(0, line.b, 0.1)
        draw.centered.color(color).circle(line.c, 0, 0.1)
    }

    private fun renderLine(line: LineSegment, color: Color) {
        setPath(line)
        renderer.color = color
        renderer.render(transformation)
    }

    private fun setPath(line: LineSegment) {
        pathHolder[0] = line.p1
        pathHolder[1] = line.p2
    }
}