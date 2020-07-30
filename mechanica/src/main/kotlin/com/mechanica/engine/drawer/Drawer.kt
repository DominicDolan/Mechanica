package com.mechanica.engine.drawer

import com.mechanica.engine.color.hex
import com.mechanica.engine.drawer.shader.AbstractDrawerShader
import com.mechanica.engine.drawer.subclasses.color.ColorDrawer
import com.mechanica.engine.drawer.subclasses.layout.OriginDrawer
import com.mechanica.engine.drawer.subclasses.rotation.RotatedDrawer
import com.mechanica.engine.drawer.subclasses.stroke.StrokeDrawer
import com.mechanica.engine.drawer.subclasses.transformation.TransformationDrawer
import com.mechanica.engine.drawer.superclass.circle.CircleDrawer
import com.mechanica.engine.drawer.superclass.image.ImageDrawer
import com.mechanica.engine.drawer.superclass.path.PathDrawer
import com.mechanica.engine.drawer.superclass.rectangle.RectangleDrawer
import com.mechanica.engine.drawer.superclass.text.TextDrawer
import com.mechanica.engine.models.Model
import com.mechanica.engine.models.PolygonModel

interface Drawer : RectangleDrawer, CircleDrawer, ImageDrawer, TextDrawer, PathDrawer {
    val origin: OriginDrawer
    val centered: Drawer
        get() = origin.relative(0.5, 0.5)

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

    fun model(model: Model, blend: Float = 0f, alphaBlend: Float = 0f, colorPassthrough: Boolean = true)

    fun shader(shader: AbstractDrawerShader, model: Model? = null)

    fun radius(r: Number): Drawer

    fun depth(z: Number): Drawer
    
    companion object {
        fun create(): Drawer {
            val data = DrawData()

            return DrawerImpl(data)
        }
    }
}