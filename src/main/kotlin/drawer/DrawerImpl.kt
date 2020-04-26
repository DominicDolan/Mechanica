package drawer

import drawer.subclasses.color.ColorDrawer2
import drawer.subclasses.color.ColorDrawerImpl
import drawer.subclasses.rotation.RotatedDrawer
import drawer.subclasses.rotation.RotatedDrawerImpl
import drawer.shader.DrawerRenderer
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

class DrawerImpl(private val matrices: Drawer.Matrices, private val renderer: DrawerRenderer) :
        RectangleDrawer by RectangleDrawerImpl(matrices, renderer),
        CircleDrawer by CircleDrawerImpl(matrices, renderer),
        ImageDrawer by ImageDrawerImpl(matrices, renderer),
        TextDrawer by TextDrawerImpl(matrices, renderer),
        PathDrawer by PathDrawerImpl(matrices.data, renderer),
        Drawer
{

    private val colorDrawer = ColorDrawerImpl(this, matrices.data)
    override val color: ColorDrawer2
        get() = colorDrawer

    private val strokeDrawer = StrokeDrawerImpl(this, matrices.data)
    override val stroke: StrokeDrawer
        get() = strokeDrawer

    private val rotatedDrawer = RotatedDrawerImpl(this, matrices.data)
    override val rotated: RotatedDrawer
        get() = rotatedDrawer

    private val layoutDrawer = LayoutDrawerImpl(this, matrices.data)
    override val layout: LayoutDrawer
        get() = layoutDrawer

    private val transformationDrawer = TransformationDrawerImpl(this, matrices.data)
    override val transformed: TransformationDrawer
        get() = transformationDrawer


    override val ui: Drawer
        get() {
            matrices.view = Game.matrices.uiView
            return this
        }
    override val world: Drawer
        get() {
            matrices.view = Game.matrices.view
            return this
        }

    override fun radius(r: Number): Drawer {
        matrices.data.radius = r.toDouble()
        return this
    }

    override fun depth(z: Number): Drawer {
        matrices.data.setDepth(z.toFloat())
        return this
    }

    override fun background() {
        with(colorDrawer) {
            GL11.glClearColor(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
        }
    }

    override fun polygon(polygon: PolygonModel) {
        renderer.colorPassthrough = true
        Drawer.draw(polygon, renderer, matrices)
    }
}