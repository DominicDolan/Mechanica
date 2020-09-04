package com.mechanica.engine.drawer

import com.mechanica.engine.drawer.shader.DrawerShader
import com.mechanica.engine.drawer.subclasses.color.ColorDrawer
import com.mechanica.engine.drawer.subclasses.color.ColorDrawerImpl
import com.mechanica.engine.drawer.subclasses.layout.OriginDrawer
import com.mechanica.engine.drawer.subclasses.layout.OriginDrawerImpl
import com.mechanica.engine.drawer.subclasses.rotation.RotatedDrawer
import com.mechanica.engine.drawer.subclasses.rotation.RotatedDrawerImpl
import com.mechanica.engine.drawer.subclasses.stroke.StrokeDrawer
import com.mechanica.engine.drawer.subclasses.stroke.StrokeDrawerImpl
import com.mechanica.engine.drawer.subclasses.transformation.TransformationDrawer
import com.mechanica.engine.drawer.subclasses.transformation.TransformationDrawerImpl
import com.mechanica.engine.drawer.superclass.circle.CircleDrawer
import com.mechanica.engine.drawer.superclass.circle.CircleDrawerImpl
import com.mechanica.engine.drawer.superclass.image.ImageDrawer
import com.mechanica.engine.drawer.superclass.image.ImageDrawerImpl
import com.mechanica.engine.drawer.superclass.path.PathDrawer
import com.mechanica.engine.drawer.superclass.path.PathDrawerImpl
import com.mechanica.engine.drawer.superclass.rectangle.RectangleDrawer
import com.mechanica.engine.drawer.superclass.rectangle.RectangleDrawerImpl
import com.mechanica.engine.drawer.superclass.text.TextDrawer
import com.mechanica.engine.drawer.superclass.text.TextDrawerImpl
import com.mechanica.engine.game.Game
import com.mechanica.engine.models.Model
import com.mechanica.engine.models.PolygonModel
import com.mechanica.engine.shader.qualifiers.Attribute
import org.lwjgl.opengl.GL11

class DrawerImpl(private val data: DrawData) :
        RectangleDrawer by RectangleDrawerImpl(data),
        CircleDrawer by CircleDrawerImpl(data),
        ImageDrawer by ImageDrawerImpl(data),
        TextDrawer by TextDrawerImpl(data),
        PathDrawer by PathDrawerImpl(data),
        Drawer
{

    private val model: Model

    init {
        val position = Attribute.location(0).vec3().createUnitQuad()
        val texCoords = Attribute.location(1).vec2().createInvertedUnitQuad()

        model = Model(position, texCoords)
    }

    private val colorDrawer = ColorDrawerImpl(this, data)
    override val color: ColorDrawer
        get() = colorDrawer

    private val strokeDrawer = StrokeDrawerImpl(this, data)
    override val stroke: StrokeDrawer
        get() = strokeDrawer

    private val rotatedDrawer = RotatedDrawerImpl(this, data)
    override val rotated: RotatedDrawer
        get() = rotatedDrawer

    private val layoutDrawer = OriginDrawerImpl(this, data)
    override val origin: OriginDrawer
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

    override fun model(model: Model, blend: Float, alphaBlend: Float, colorPassthrough: Boolean) {
        data.blend = blend
        data.alphaBlend = alphaBlend
        data.colorPassthrough = colorPassthrough
        data.draw(model)
    }

    override fun shader(shader: DrawerShader, model: Model?) {
        data.draw(model ?: this.model, shader)
    }
}