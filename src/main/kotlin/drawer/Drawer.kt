package drawer

import drawer.subclasses.color.ColorDrawer2
import drawer.subclasses.rotation.RotatedDrawer
import drawer.shader.DrawerRenderer
import drawer.subclasses.layout.LayoutDrawer
import drawer.subclasses.stroke.StrokeDrawer
import drawer.subclasses.transformation.TransformationDrawer
import drawer.superclass.circle.CircleDrawer
import drawer.superclass.image.ImageDrawer
import drawer.superclass.path.PathDrawer
import drawer.superclass.rectangle.RectangleDrawer
import drawer.superclass.text.TextDrawer
import game.Game
import gl.models.Model
import gl.models.PolygonModel
import org.joml.Matrix4f
import util.colors.hex

interface Drawer : RectangleDrawer, CircleDrawer, ImageDrawer, TextDrawer, PathDrawer {
    val layout: LayoutDrawer
    val centered: Drawer
        get() = layout.origin(0.5, 0.5)

    val color: ColorDrawer2

    val black: ColorDrawer2
        get() {
            color(hex(0x000000FF))
            return color
        }
    val white: ColorDrawer2
        get() {
            color(hex(0xFFFFFFFF))
            return color
        }
    val grey: ColorDrawer2
        get() {
            color(hex(0x808080FF))
            return color
        }
    val darkGrey: ColorDrawer2
        get() {
            color(hex(0x696969FF))
            return color
        }
    val lightGrey: ColorDrawer2
        get() {
            color(hex(0xD3D3D3FF))
            return color
        }
    val red: ColorDrawer2
        get() {
            color(hex(0xFF0000FF))
            return color
        }
    val green: ColorDrawer2
        get() {
            color(hex(0x00FF00FF))
            return color
        }
    val blue: ColorDrawer2
        get() {
            color(hex(0x0000FFFF))
            return color
        }
    val magenta: ColorDrawer2
        get() {
            color(hex(0xFF00FFFF))
            return color
        }
    val cyan: ColorDrawer2
        get() {
            color(hex(0x00FFFFFF))
            return color
        }
    val yellow: ColorDrawer2
        get() {
            color(hex(0xFFFF00FF))
            return color
        }

    val stroke: StrokeDrawer

    val rotated: RotatedDrawer

    val ui: Drawer
    val world: Drawer

    val transformed: TransformationDrawer

    fun background()

    fun polygon(polygon: PolygonModel)

    fun radius(r: Number): Drawer

    fun depth(z: Number): Drawer
    
    companion object {
        fun create(): Drawer {
            val matrices = Matrices(DrawData(), Game.matrices.view, Game.matrices.projection)
            val renderer = DrawerRenderer()

            return DrawerImpl(matrices, renderer)
        }

        fun draw(model: Model, renderer: DrawerRenderer, matrices: Matrices) {
            renderer.color = matrices.data.fillColor
            renderer.radius = matrices.data.radius.toFloat()
            renderer.render(model, matrices)

            if (!matrices.data.noReset) {
                matrices.data.rewind()
                renderer.rewind()
            }
        }
    }
    data class Matrices(val data: DrawData, var view: Matrix4f, var projection: Matrix4f)

}