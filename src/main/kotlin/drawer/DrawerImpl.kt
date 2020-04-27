package drawer

import drawer.subclasses.color.ColorDrawer
import drawer.subclasses.color.ColorDrawerImpl
import drawer.subclasses.rotation.RotatedDrawer
import drawer.subclasses.rotation.RotatedDrawerImpl
import drawer.subclasses.layout.LayoutDrawer
import drawer.subclasses.layout.LayoutDrawerImpl
import drawer.subclasses.stroke.StrokeDrawer
import drawer.subclasses.stroke.StrokeDrawerImpl
import drawer.subclasses.transformation.TransformationDrawer
import drawer.subclasses.transformation.TransformationDrawerImpl
import drawer.superclass.circle.CircleDrawer
import drawer.superclass.circle.CircleDrawerImpl
import drawer.superclass.image.ImageDrawer
import drawer.superclass.image.ImageDrawerImpl
import drawer.superclass.path.PathDrawer
import drawer.superclass.path.PathDrawerImpl
import drawer.superclass.rectangle.RectangleDrawer
import drawer.superclass.rectangle.RectangleDrawerImpl
import drawer.superclass.text.TextDrawer
import drawer.superclass.text.TextDrawerImpl
import game.Game
import gl.models.PolygonModel
import org.lwjgl.opengl.GL11

class DrawerImpl(private val data: DrawData) :
        RectangleDrawer by RectangleDrawerImpl(data),
        CircleDrawer by CircleDrawerImpl(data),
        ImageDrawer by ImageDrawerImpl(data),
        TextDrawer by TextDrawerImpl(data),
        PathDrawer by PathDrawerImpl(data),
        Drawer
{

    private val colorDrawer = ColorDrawerImpl(this, data)
    override val color: ColorDrawer
        get() = colorDrawer

    private val strokeDrawer = StrokeDrawerImpl(this, data)
    override val stroke: StrokeDrawer
        get() = strokeDrawer

    private val rotatedDrawer = RotatedDrawerImpl(this, data)
    override val rotated: RotatedDrawer
        get() = rotatedDrawer

    private val layoutDrawer = LayoutDrawerImpl(this, data)
    override val layout: LayoutDrawer
        get() = layoutDrawer

    private val transformationDrawer = TransformationDrawerImpl(this, data)
    override val transformed: TransformationDrawer
        get() = transformationDrawer


    override val ui: Drawer
        get() {
            data.viewMatrix = Game.matrices.uiView
            return this
        }
    override val world: Drawer
        get() {
            data.viewMatrix = Game.matrices.view
            return this
        }

    override fun radius(r: Number): Drawer {
        data.radius = r.toFloat()
        return this
    }

    override fun depth(z: Number): Drawer {
        data.setDepth(z.toFloat())
        return this
    }

    override fun background() {
        with(colorDrawer) {
            GL11.glClearColor(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
        }
    }

    override fun polygon(polygon: PolygonModel) {
        data.colorPassthrough = true
        data.draw(polygon)
    }
}