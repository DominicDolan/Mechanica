package com.mechanica.engine.drawer

import com.mechanica.engine.context.loader.MechanicaLoader
import com.mechanica.engine.drawer.shader.DrawerRenderer
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
import com.mechanica.engine.shader.attributes.AttributeArray
import com.mechanica.engine.utils.createInvertedUnitSquareVectors
import com.mechanica.engine.utils.createUnitSquareVectors

class DrawerImpl(private val state: DrawState,
                 private val renderer: DrawerRenderer) :
        RectangleDrawer by RectangleDrawerImpl(state, renderer),
        CircleDrawer by CircleDrawerImpl(state, renderer),
        ImageDrawer by ImageDrawerImpl(state, renderer),
        TextDrawer by TextDrawerImpl(state, renderer),
        PathDrawer by PathDrawerImpl(state),
        Drawer
{

    private val model: Model

    init {
        val position = AttributeArray.createPositionArray(createUnitSquareVectors())
        val texCoords = AttributeArray.createTextureArray(createInvertedUnitSquareVectors())

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
            state.viewMatrix.variable = Game.matrices.uiCamera
            return this
        }
    override val world: Drawer
        get() {
            state.viewMatrix.variable = Game.matrices.worldCamera
            return this
        }

    override fun radius(r: Number): Drawer {
        state.setRadius(r)
        return this
    }

    override fun depth(z: Number): Drawer {
        state.setDepth(z.toFloat())
        return this
    }

    override fun background() {
        with(colorDrawer) {
            MechanicaLoader.graphicsLoader.clearColor(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
        }
    }

    override fun polygon(polygon: PolygonModel) {
        state.setModel(polygon)
        renderer.render(state, 0f, 0f, true)
    }

    override fun model(model: Model, blend: Float, alphaBlend: Float, colorPassthrough: Boolean) {
        state.setModel(model)
        renderer.render(state, blend, alphaBlend, colorPassthrough)
    }

    override fun shader(shader: DrawerShader, model: Model?) {
        shader.fragment.color.set(state.color.fill)
        shader.fragment.size.set(state.shader.cornerSize)
        shader.fragment.radius.value = state.shader.radius.value.toFloat()

        shader.render(
                model ?: state.shader.model.variable,
                state.transformation.getTransformationMatrix(),
                state.projectionMatrix.variable,
                state.viewMatrix.variable
        )
        state.reset()
    }
}