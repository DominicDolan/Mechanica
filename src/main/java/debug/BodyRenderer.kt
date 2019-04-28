package debug

import compatibility.toDegrees
import display.Game
import models.Model
import org.jbox2d.callbacks.DebugDraw
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.EdgeShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.collision.shapes.Shape
import org.jbox2d.common.Color3f
import org.jbox2d.common.Transform
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.Body
import renderer.Painter

object BodyRenderer : DebugDraw(null) {

    private var g: Painter? = null
    const val debugColor: Long = 0x00FF4070

    fun init() {
        if (Game.debug) {
            Game.world.setDebugDraw(BodyRenderer)
            BodyRenderer.flags = e_shapeBit
        }
    }

    fun update(g: Painter) {
        g.color = debugColor
        this.g = g
    }

    override fun drawSolidCircle(center: Vec2?, radius: Float, axis: Vec2?, color: Color3f?) {
        val c = center?:Vec2()
        g?.drawCircle(c.x, c.y, radius, radius/5f)
    }


    override fun drawSegment(point1: Vec2?, point2: Vec2?, color: Color3f?) {
        val p1 = point1?:Vec2()
        val p2 = point2?:Vec2()
        g?.drawLine(0.1, p1.x, p1.y, p2.x, p2.y)
    }


    override fun drawSolidPolygon(vertices: Array<out Vec2>?, vertexCount: Int, color: Color3f?) {
        val v = vertices?: emptyArray()
        g?.drawPolygon(v, 0.1, vertexCount, true)
    }

    override fun drawCircle(center: Vec2?, radius: Float, color: Color3f?) { }

    override fun drawTransform(transform: Transform?) {
    }

    override fun drawPoint(p0: Vec2?, p1: Float, p2: Color3f?) {
    }
    override fun drawString(p0: Float, p1: Float, string: String?, color: Color3f?) {
    }

}
