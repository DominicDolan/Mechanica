package debug

import compatibility.toDegrees
import models.Model
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.EdgeShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.collision.shapes.Shape
import org.jbox2d.dynamics.Body
import renderer.Painter

val debugBodies = ArrayList<BodyProperties>()
const val debugColor: Long = 0x00FF4070

fun renderPhysicsBodies(g: Painter) {
    val previousColor = g.color
    g.color = debugColor

    for (b in debugBodies) {
        when (b.shape) {
            is CircleShape -> g.fillCircle(b.body.position.x, b.body.position.y, b.shape.radius)
            is PolygonShape, is EdgeShape -> b.model?.let {
                g.fillRotatedPolygon(it, b.body.position.x, b.body.position.y, b.body.angle.toDegrees())
            }
        }
    }

    g.color = previousColor
}

data class BodyProperties (
        val body: Body,
        val shape:  Shape,
        var model:  Model?
)