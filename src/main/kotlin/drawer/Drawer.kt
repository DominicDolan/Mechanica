package drawer

import drawer.subclasses.color.ColorDrawer
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
import gl.models.Model
import gl.models.PolygonModel
import util.colors.hex

interface Drawer : RectangleDrawer, CircleDrawer, ImageDrawer, TextDrawer, PathDrawer {
    val layout: LayoutDrawer
    val centered: Drawer
        get() = layout.origin(0.5, 0.5)

    val color: ColorDrawer

    val black: ColorDrawer
        get() {
            color(hex(0x000000FF))
            return color
        }
    val white: ColorDrawer
        get() {
            color(hex(0xFFFFFFFF))
            return color
        }
    val grey: ColorDrawer
        get() {
            color(hex(0x808080FF))
            return color
        }
    val darkGrey: ColorDrawer
        get() {
            color(hex(0x696969FF))
            return color
        }
    val lightGrey: ColorDrawer
        get() {
            color(hex(0xD3D3D3FF))
            return color
        }
    val red: ColorDrawer
        get() {
            color(hex(0xFF0000FF))
            return color
        }
    val green: ColorDrawer
        get() {
            color(hex(0x00FF00FF))
            return color
        }
    val blue: ColorDrawer
        get() {
            color(hex(0x0000FFFF))
            return color
        }
    val magenta: ColorDrawer
        get() {
            color(hex(0xFF00FFFF))
            return color
        }
    val cyan: ColorDrawer
        get() {
            color(hex(0x00FFFFFF))
            return color
        }
    val yellow: ColorDrawer
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
            val data = DrawData()

            return DrawerImpl(data)
        }
    }
}