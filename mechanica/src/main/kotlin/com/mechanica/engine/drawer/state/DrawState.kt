package com.mechanica.engine.drawer.state

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.InlineColor
import com.mechanica.engine.game.Game
import com.mechanica.engine.models.Model
import com.mechanica.engine.models.TextModel

abstract class AbstractDrawState : Resettable {
    protected val list = DrawStateVariableList()

    val hasChanged: Boolean
        get() {
            list.forEach {
                if (it != null) {
                    return true
                }
            }
            return false
        }

    init {
        list.reset()
    }

    override fun reset() {
        list.reset()
    }
}

class DrawState : AbstractDrawState() {

    val viewMatrix = list.addVariable(Game.matrices.worldCamera) { variable = Game.matrices.worldCamera}

    val projectionMatrix = list.addVariable(Game.matrices.projection) { variable = Game.matrices.projection }

    val origin: OriginState = list.add(OriginState())
    val transformation: TransformationState = list.add(TransformationState(origin))
    val color: ColorState = list.add(ColorState())
    val shader: ShaderState = list.add(ShaderState())

    var noReset = false

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

    fun setRadius(radius: Number) {
        shader.radius.value = radius.toDouble()
    }

    fun setModel(model: Model) {
        shader.model.variable = model
    }

    fun setFillColor(color: Color) {
        this.color.fill.set(color)
    }

    fun setFillColor(color: InlineColor) {
        this.color.fill.set(color)
    }


    fun setStrokeColor(color: Color) {
        this.color.fill.set(color)
    }

    fun setStrokeColor(color: InlineColor) {
        this.color.fill.set(color)
    }

    fun setStrokeWidth(width: Number) {
        shader.strokeWidth.value = width.toDouble()
    }

    override fun reset() {
        if (!noReset) super.reset()
    }
}