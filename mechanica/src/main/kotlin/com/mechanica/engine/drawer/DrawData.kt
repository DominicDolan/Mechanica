package com.mechanica.engine.drawer

import com.mechanica.engine.color.DynamicColor
import com.mechanica.engine.font.Font
import com.mechanica.engine.drawer.shader.DrawerRenderer
import com.mechanica.engine.game.Game
import com.mechanica.engine.gl.models.Model
import com.mechanica.engine.gl.models.TextModel
import com.mechanica.engine.resources.Res
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.LightweightVector
import com.mechanica.engine.unit.vector.Vector
import org.joml.Matrix4f
import org.joml.Vector3f

class DrawData {

    var viewMatrix: Matrix4f = Game.matrices.view
    var projectionMatrix: Matrix4f = Game.matrices.projection

    private val renderer = DrawerRenderer()
    private val transformation = Matrix4f().identity()

    private val translation: Vector3f = Vector3f()
    private val pivot: Vector3f = Vector3f()
    private val unpivot: Vector3f = Vector3f()
    private val scale: Vector3f = Vector3f(1f, 1f, 1f)
    private val zAxis = Vector3f(0f, 0f, 1f)
    private var rx = 0f
    private var ry = 0f
    private var rz = 0f

    val scaleX get() = scale.x
    val scaleY get() = scale.y

    val translateX get() = translation.x
    val translateY get() = translation.y
    val translateZ get() = translation.z

    var strokeWidth: Double = 0.0
        set(value) {
            field = value
            renderer.strokeWidth = value
        }

    val strokeColor = DynamicColor(0.0, 0.0, 0.0, 1.0)

    val fillColor = DynamicColor(0.0, 0.0, 0.0, 1.0)

    val modelOrigin = OriginVector()

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

    private val fontMap = HashMap<Font, TextModel>()

    private var font: Font = Font(Res.font["Roboto-Regular.ttf"]).also { fontMap[it] = TextModel(it) }

    val textModel: TextModel
        get() = fontMap[font] ?: TextModel(font).also { fontMap[font] = it }

    fun setTranslate(x: Float, y: Float) {
        translation.set(x, y, translateZ)
    }

    fun setRotate(angle: Float) {
        rz = angle
    }

    fun setScale(x: Float, y: Float) {
        scale.set(x, y, 0f)
    }

    fun setDepth(z: Float) {
        translation.set(translateX, translateY, -z)
    }

    fun getTransformationMatrix(matrix: Matrix4f): Matrix4f {
        matrix.translate(translation)

        if (rz != 0f)
            matrix.rotate(rz, zAxis)

        if (modelOrigin.x != 0.0 || modelOrigin.y != 0.0) {
            val pivotX = modelOrigin.x.toFloat()*scale.x
            val pivotY = modelOrigin.y.toFloat()*scale.y
            matrix.translate(-pivotX, -pivotY, 0f)
        }

        matrix.scale(scale)
        pivot.set(0f, 0f, 0f)
        unpivot.set(0f, 0f, 0f)
        rx = 0f
        ry = 0f
        rz = 0f
        return matrix
    }

    fun draw(model: Model) {
        renderer.color = fillColor
        renderer.radius = radius

        getTransformationMatrix(transformation)
        renderer.render(model, transformation, viewMatrix, projectionMatrix)

        if (!noReset) {
            rewind()
        }
    }

    fun rewind() {
        setRotate(0f)
        setTranslate(0f, 0f)
        setDepth(0f)
        setScale(1f, 1f)

        radius = 0f
        noReset = false

        viewMatrix = Game.matrices.view
        projectionMatrix = Game.matrices.projection

        transformation.identity()
        renderer.rewind()
        modelOrigin.reset()
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