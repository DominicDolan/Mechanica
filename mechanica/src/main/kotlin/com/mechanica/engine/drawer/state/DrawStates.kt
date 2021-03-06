package com.mechanica.engine.drawer.state

import com.cave.library.angle.radians
import com.cave.library.color.Color
import com.cave.library.matrix.mat4.Matrix4
import com.cave.library.vector.arrays.Vector2Arrays
import com.mechanica.engine.drawer.shader.DrawerScript
import com.mechanica.engine.shaders.attributes.AttributeArray
import com.mechanica.engine.shaders.models.Model

class TransformationState(private val origin: OriginState) : AbstractDrawState() {
    private val matrix = list.add(Matrix4.identity()) { it.identity() }
    var userMatrix: Matrix4? = null

    val translation = list.addVector3(0.0)
    val scale = list.addVector3(1.0)

    var rotation = list.addRadian(0.0.radians)

    val skew = list.addAngleVector2(0.0.radians)

    fun getTransformationMatrix(): Matrix4 {
        val matrix: Matrix4 = matrix.variable
        matrix.translation.set(translation.variable)

        if (rotation.wasChanged) {
            matrix.rotation.apply(rotation.value)
        }

        if (skew.wasChanged) {
            matrix.skew.apply(skew.x, skew.y)
        }

        addOriginToMatrix(matrix)

        matrix.scale.apply(scale.variable)

        userMatrix?.let {
            matrix *= it
        }
        userMatrix = null
        return matrix
    }

    private fun addOriginToMatrix(matrix: Matrix4) {
        if (origin.wasChanged) {
            val pivotX = origin.normalized.x*scale.x + origin.relative.x
            val pivotY = origin.normalized.y*scale.y + origin.relative.y
            matrix.translation.apply(-pivotX, -pivotY, 0.0)
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
        val position = AttributeArray.createPositionArray(Vector2Arrays.createUnitSquare())
        val texCoords = AttributeArray.createTextureArray(Vector2Arrays.createInvertedUnitSquare())

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
