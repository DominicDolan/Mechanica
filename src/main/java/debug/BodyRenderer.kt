package debug

import display.Game
import org.jbox2d.callbacks.DebugDraw
import org.jbox2d.common.Color3f
import org.jbox2d.common.Transform
import org.jbox2d.common.Vec2
import graphics.drawer.Drawer
import util.extensions.toVec

object BodyRenderer : DebugDraw(null) {

    private var draw: Drawer? = null
    const val debugColor: Long = 0x00FF4070

    fun init() {
        if (Game.debug) {
            Game.world.setDebugDraw(BodyRenderer)
            BodyRenderer.flags = e_shapeBit
        }
    }

    fun update(draw: Drawer) {
        draw.color(debugColor)
        this.draw = draw
    }

    override fun drawSolidCircle(center: Vec2?, radius: Float, axis: Vec2?, color: Color3f?) {
        val c = center?:Vec2()
        draw?.stroke?.invoke(radius/5.0)?.circle(c.x, c.y, radius)
    }


    override fun drawSegment(point1: Vec2?, point2: Vec2?, color: Color3f?) {
        val p1 = point1?:Vec2()
        val p2 = point2?:Vec2()
        draw?.stroke?.invoke(0.1)?.line(p1.toVec(), p2.toVec())
    }


    override fun drawSolidPolygon(vertices: Array<out Vec2>?, vertexCount: Int, color: Color3f?) {
        val v = vertices?.slice(0 until vertexCount)?: emptyList()
        draw?.let {draw ->
            draw.stroke(0.1).path(v.map { it.toVec()})
            draw.stroke(0.1).line(v.last().toVec(), v.first().toVec())
        }
    }

    override fun drawCircle(center: Vec2?, radius: Float, color: Color3f?) { }

    override fun drawTransform(transform: Transform?) {
    }

    override fun drawPoint(p0: Vec2?, p1: Float, p2: Color3f?) {
    }
    override fun drawString(p0: Float, p1: Float, string: String?, color: Color3f?) {
    }

}
