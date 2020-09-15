package com.mechanica.engine.drawer

import com.mechanica.engine.color.DynamicColor
import com.mechanica.engine.drawer.shader.DrawerRenderer
import com.mechanica.engine.drawer.shader.DrawerShader
import com.mechanica.engine.game.Game
import com.mechanica.engine.models.Model
import com.mechanica.engine.models.TextModel
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.tan

class DrawData {

    var viewMatrixWasSet = false
        private set
    var viewMatrix: Matrix4f = Game.matrices.worldCamera
        set(value) {
            viewMatrixWasSet = true
            field = value
        }
    private var projectionMatrix: Matrix4f = Game.matrices.projection

    private val renderer = DrawerRenderer()
    private val defaultTransformation = Matrix4f().identity()
    private val spareTransformation = Matrix4f().identity()
    var transformation: Matrix4f? = null

    private val translation: Vector3f = Vector3f()
    private val pivot: Vector3f = Vector3f()
    private val scale: Vector3f = Vector3f(1f, 1f, 1f)
    private val zAxis = Vector3f(0f, 0f, 1f)
    private var rx = 0f
    private var ry = 0f
    private var rz = 0f

    private var skewX = 0f
    private var skewY = 0f

    val scaleX get() = scale.x
    val scaleY get() = scale.y

    private val translateX get() = translation.x
    private val translateY get() = translation.y
    private val translateZ get() = translation.z

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

    val normalizedOrigin = OriginVector()
    val relativeOrigin = OriginVector()

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
        translation.set(translation.x + x, translation.y + y, translateZ)
    }

    fun setRotate(angle: Float) {
        rz += angle
    }

    fun setScale(x: Float, y: Float) {
        scale.set(scale.x*x, scale.y*y, 0f)
    }

    fun setSkew(x: Float, y: Float) {
        skewX = x
        skewY = y
    }

    fun setDepth(z: Float) {
        translation.set(translateX, translateY, translation.z-z)
    }

    fun getTransformationMatrix(matrix: Matrix4f): Matrix4f {
        matrix.translate(translation)

        if (rz != 0f)
            matrix.rotate(rz, zAxis)

        addSkewToMatrix(matrix)
        addModelOriginToMatrix(matrix)

        matrix.scale(scale)
        pivot.set(0f, 0f, 0f)
        return matrix
    }

    fun draw(model: Model, shader: DrawerShader = renderer.shader) {
        renderer.color = fillColor
        renderer.radius = radius

        getTransformationMatrix(defaultTransformation)
        transformation?.let { defaultTransformation.mul(it) }

        shader.render(model, defaultTransformation, projectionMatrix, viewMatrix)

        if (!noReset) {
            rewind()
        }
    }

    private fun addModelOriginToMatrix(matrix: Matrix4f) {
        if (normalizedOrigin.wasSet || relativeOrigin.wasSet) {
            val pivotX = normalizedOrigin.x.toFloat()*scale.x + relativeOrigin.x.toFloat()
            val pivotY = normalizedOrigin.y.toFloat()*scale.y + relativeOrigin.y.toFloat()
            matrix.translate(-pivotX, -pivotY, 0f)
        }
    }

    private fun addSkewToMatrix(matrix: Matrix4f) {
        if (skewX != 0f || skewY != 0f) {
            spareTransformation.m10(tan(-skewX))
            spareTransformation.m01(tan(skewY))

            matrix.mul(spareTransformation)
        }
    }

    fun rewind() {
        rz = 0f
        fillColor.a = 1.0
        translation.set(0f, 0f, 0f)
        scale.set(1f, 1f, 1f)
        skewX = 0f
        skewY = 0f

        radius = 0f
        noReset = false

        viewMatrix = Game.matrices.worldCamera
        viewMatrixWasSet = false
        projectionMatrix = Game.matrices.projection

        strokeColorWasSet = false
        fillColorWasSet = false

        defaultTransformation.identity()
        transformation = null
        renderer.rewind()
        normalizedOrigin.reset()
        relativeOrigin.reset()
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

        fun set(vector: LightweightVector) {
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