package drawer

import drawer.subclasses.color.ColorDrawer
import drawer.subclasses.rotation.RotatedDrawer
import graphics.Image
import graphics.Polygon
import util.colors.hex
import util.units.Vector

interface Drawer {

    fun background()

    fun rectangle(x: Number, y: Number, width: Number, height: Number)

    fun circle(x: Number, y: Number, radius: Number) {
        val diameter = 2.0*radius.toDouble()
        ellipse(x, y, diameter, diameter)
    }

    fun circle(position: Vector, radius: Number) = circle(position.x, position.y, radius)

    fun ellipse(x: Number, y: Number, width: Number, height: Number)

    fun text(text: String, fontSize: Number, x: Number, y: Number)

    fun image(image: Image, x: Number, y: Number, width: Number, height: Number)

    fun polygon(polygon: Polygon, x: Number = 0.0, y: Number = 0.0, scaleWidth: Number = 1.0, scaleHeight: Number = 1.0)

    fun path(path: List<Vector>, x: Number = 0.0, y: Number = 0.0, scaleWidth: Number = 1.0, scaleHeight: Number = 1.0)

    fun line(x1: Number, y1: Number, x2: Number, y2: Number)

    fun line(p1: Vector, p2: Vector) = line(p1.x, p1.y, p2.x, p2.y)

    val normal: Drawer
    val centered: Drawer

//    @Suppress("LeakingThis") // Leaking this is okay because no processing is being done on 'this' inside the constructor
//    val positional: Positional = Positional(this)
//        get() {
//            Drawer.isLayoutSet = true
//            return field
//        }

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
}