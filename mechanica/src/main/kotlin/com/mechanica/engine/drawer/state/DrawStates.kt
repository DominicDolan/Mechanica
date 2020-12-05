package com.mechanica.engine.drawer.state

import com.mechanica.engine.color.Color
import com.mechanica.engine.drawer.shader.DrawerScript
import com.mechanica.engine.models.Model
import com.mechanica.engine.shader.attributes.AttributeArray
import com.mechanica.engine.utils.createInvertedUnitSquareVectors
import com.mechanica.engine.utils.createUnitSquareVectors
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.tan

class TransformationState(private val origin: OriginState) : AbstractDrawState() {
    private val matrix = list.add(Matrix4f().identity()) { it.identity() }
    var userMatrix: Matrix4f? = null

    val translation = list.addVector3(0f)
    val scale = list.addVector3(1f)

    private val zAxis = Vector3f(0f, 0f, 1f)
    var rotation = list.addDouble(0.0)

    private val skewMatrix = Matrix4f().identity()
    val skew = list.addVector2(0.0)

    fun getTransformationMatrix(): Matrix4f {
        val matrix = matrix.variable
        matrix.translate(translation.variable)

        if (rotation.wasChanged)
            matrix.rotate(rotation.value.toFloat(), zAxis)

        addSkewToMatrix(matrix)
        addOriginToMatrix(matrix)

        matrix.scale(scale.variable)

        userMatrix?.let { matrix.mul(it) }
        userMatrix = null
        return matrix
    }

    private fun addSkewToMatrix(matrix: Matrix4f) {
        if (skew.wasChanged) {
            skewMatrix.m10(tan(-skew.x.toFloat()))
            skewMatrix.m01(tan(skew.y.toFloat()))

            matrix.mul(skewMatrix)
        }
    }

    private fun addOriginToMatrix(matrix: Matrix4f) {
        if (origin.wasChanged) {
            val pivotX = origin.normalized.x.toFloat()*scale.x + origin.relative.x.toFloat()
            val pivotY = origin.normalized.y.toFloat()*scale.y + origin.relative.y.toFloat()
            matrix.translate(-pivotX, -pivotY, 0f)
        }
    }
}

class OriginState : AbstractDrawState() {
    val normalized = list.addVector2()
    val relative = list.addVector2()

    val wasChanged: Boolean
        get() = normalized.wasChanged || relative.wasChanged
}

class ShaderState : AbstractDrawState() {
    val radius = list.addDouble(0.0)
    val strokeWidth = list.addDouble(0.0)
    val cornerSize = list.addVector2(0.0)

    private val defaultModel: Model
    val model: GenericVariable<Model>

    init {
        val position = AttributeArray.createPositionArray(createUnitSquareVectors())
        val texCoords = AttributeArray.createTextureArray(createInvertedUnitSquareVectors())

        defaultModel = Model(position, texCoords)

        model = list.addVariable(defaultModel) { variable = defaultModel }
    }
}

class ColorState : AbstractDrawState() {
    val fill = list.addColor()
    val stroke = list.addColor(resetColor = Color.rgba(1.0, 1.0, 1.0, 0.0))

    fun assignColorsToFragment(fragmentScript: DrawerScript) {
        fragmentScript.color.set(fill)
    }
}
