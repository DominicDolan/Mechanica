package com.mechanica.engine.drawer

import com.mechanica.engine.drawer.shader.DrawerShader
import com.mechanica.engine.drawer.state.DrawState
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
import com.mechanica.engine.vertices.AttributeArray
import org.lwjgl.opengl.GL11

class DrawerImpl(private val state: DrawState) :
        RectangleDrawer by RectangleDrawerImpl(state),
        CircleDrawer by CircleDrawerImpl(state),
        ImageDrawer by ImageDrawerImpl(state),
        TextDrawer by TextDrawerImpl(state),
        PathDrawer by PathDrawerImpl(state),
        Drawer
{

    private val model: Model

    init {
        val position = AttributeArray.createFrom(Attribute.position).createUnitQuad()
        val texCoords = AttributeArray.createFrom(Attribute.textureCoords).createInvertedUnitQuad()

        model = Model(position, texCoords)
    }

    private val colorDrawer = ColorDrawerImpl(this, state)
    override val color: ColorDrawer
        get() = colorDrawer

    private val strokeDrawer = StrokeDrawerImpl(this, state)
    override val stroke: StrokeDrawer
        get() = strokeDrawer

    private val rotatedDrawer = RotatedDrawerImpl(this, state)
    override val rotated: RotatedDrawer
        get() = rotatedDrawer

    private val originDrawer = OriginDrawerImpl(this, state)
    override val origin: OriginDrawer
        get() = originDrawer

    private val transformationDrawer = TransformationDrawerImpl(this, state)
    override val transformed: TransformationDrawer
        get() = transformationDrawer


    override val ui: Drawer
        get() {
            state.viewMatrix = Game.matrices.uiCamera
            return this
        }
    override val world: Drawer
        get() {
            state.viewMatrix = Game.matrices.worldCamera
            return this
        }

    override fun radius(r: Number): Drawer {
        state.radius = r.toFloat()
        return this
    }

    override fun depth(z: Number): Drawer {
        state.transformation.setDepth(z.toFloat())
        return this
    }

    override fun background() {
        with(colorDrawer) {
            GL11.glClearColor(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
        }
    }

    override fun polygon(polygon: PolygonModel) {
        state.colorPassthrough = true
        state.draw(polygon)
    }

    override fun model(model: Model, blend: Float, alphaBlend: Float, colorPassthrough: Boolean) {
        state.blend = blend
        state.alphaBlend = alphaBlend
        state.colorPassthrough = colorPassthrough
        state.draw(model)
    }

    override fun shader(shader: DrawerShader, model: Model?) {
        state.draw(model ?: this.model, shader)
    }
}