package com.mechanica.engine.drawer.state

import com.mechanica.engine.color.DynamicColor
import com.mechanica.engine.drawer.shader.DrawerRenderer
import com.mechanica.engine.drawer.shader.DrawerShader
import com.mechanica.engine.game.Game
import com.mechanica.engine.models.Model
import com.mechanica.engine.models.TextModel
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.InlineVector
import com.mechanica.engine.unit.vector.Vector
import org.joml.Matrix4f

class DrawState {
    var viewMatrixWasSet = false
        private set
    var viewMatrix: Matrix4f = Game.matrices.worldCamera
        set(value) {
            viewMatrixWasSet = true
            field = value
        }
    private var projectionMatrix: Matrix4f = Game.matrices.projection

    private val renderer = DrawerRenderer()

    val origin = OriginState()
    val transformation = TransformationState(origin)

    var strokeWidth: Double = 0.0
        set(value) {
            field = value
            renderer.strokeWidth = value
        }

    var strokeColorWasSet = false
    val strokeColor = DynamicColor(0.0, 0.0, 0.0, 1.0)
        get() {
            strokeColorWasSet = true
            return field
        }

    var fillColorWasSet = false
    val fillColor = DynamicColor(0.0, 0.0, 0.0, 1.0)
        get() {
            fillColorWasSet = true
            return field
        }

    var radius: Float = 0f

    var blend: Float
        get() = renderer.blend
        set(value) { renderer.blend = value}

    var alphaBlend: Float
        get() = renderer.alphaBlend
        set(value) { renderer.alphaBlend = value}

    var colorPassthrough: Boolean
        get() = renderer.colorPassthrough
        set(value) { renderer.colorPassthrough = value}

    var noReset = false

    val cornerSize: DynamicVector
        get() = renderer.size

    val stringHolderModel: TextModel = TextModel("")
    val textHolderModel: TextModel = TextModel("")

    fun setTranslate(x: Float, y: Float) {
        val translation = transformation.translation
        translation.set(translation.x + x, translation.y + y)
    }

    fun setRotate(angle: Double) {
        transformation.rotation.value += angle
    }

    fun setScale(x: Float, y: Float) {
        val scale = transformation.scale
        scale.set(scale.x*x, scale.y*y, 0f)
    }

    fun setSkew(x: Float, y: Float) {
        val skew = transformation.skew
        skew.set(skew.x + x, skew.y + y)
    }

    fun setDepth(z: Float) {
        val translation = transformation.translation
        translation.set(translation.x, translation.y, translation.z-z)
    }

    fun draw(model: Model, shader: DrawerShader = renderer.shader) {
        shader.fragment.color.set(fillColor)
        shader.fragment.radius.value = radius

        val matrix = transformation.getTransformationMatrix()

        shader.render(model, matrix, projectionMatrix, viewMatrix)

        if (!noReset) {
            rewind()
        }
    }

    fun rewind() {
        transformation.reset()
        origin.reset()

        fillColor.a = 1.0

        radius = 0f
        noReset = false

        viewMatrix = Game.matrices.worldCamera
        viewMatrixWasSet = false
        projectionMatrix = Game.matrices.projection

        strokeColorWasSet = false
        fillColorWasSet = false

        renderer.rewind()
    }

    inner class OriginVector : Vector {
        var wasSet: Boolean = true
        override var x: Double = 0.0
            set(value) {
                field = value
                wasSet = true
            }
        override var y: Double = 0.0
            set(value) {
                field = value
                wasSet = true
            }

        fun set(x: Double, y: Double) {
            this.x = x
            this.y = y
        }

        fun set(vector: Vector) {
            this.x = vector.x
            this.y = vector.y
        }

        fun set(vector: InlineVector) {
            this.x = vector.x
            this.y = vector.y
        }

        fun reset() {
            x = 0.0
            y = 0.0
            wasSet = false
        }

    }

}