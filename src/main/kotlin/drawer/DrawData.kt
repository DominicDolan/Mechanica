package drawer

import font.Font
import gl.models.TextModel
import org.joml.Matrix4f
import org.joml.Vector3f
import resources.Res
import util.colors.Color
import util.colors.DynamicColor
import util.colors.rgba2Hex
import util.units.LightweightVector
import util.units.Vector

class DrawData {
    private val translation: Vector3f = Vector3f()
    private val pivot: Vector3f = Vector3f()
    private val unpivot: Vector3f = Vector3f()
    private val scale: Vector3f = Vector3f(1f, 1f, 1f)
    private val zAxis = Vector3f(0f, 0f, 1f)
    private var rx = 0f
    private var ry = 0f
    private var rz = 0f

    private var scheduleRotation = true

    val scaleX get() = scale.x
    val scaleY get() = scale.y

    val translateX get() = translation.x
    val translateY get() = translation.y
    val translateZ get() = translation.z

    var strokeWidth: Double = 0.0
    val strokeColor = DynamicColor(0.0, 0.0, 0.0, 1.0)

    val fillColor = DynamicColor(0.0, 0.0, 0.0, 1.0)

    val modelOrigin = OriginVector()

    var radius: Double = 0.0

    var noReset = false

    private val fontMap = HashMap<Font, TextModel>()

    private var font: Font = Font(Res.font["Roboto-Regular.ttf"]).also { fontMap[it] = TextModel(it) }

    val textModel: TextModel
        get() = fontMap[font] ?: TextModel(font).also { fontMap[font] = it }

    fun setTranslate(x: Float, y: Float) {
        translation.set(x, y, translateZ)
    }

    fun setRotate(angle: Float) {
        rz = angle
        scheduleRotation = true
    }

    fun setScale(x: Float, y: Float) {
        scale.set(x, y, 0f)
    }

    fun setDepth(z: Float) {
        translation.set(translateX, translateY, -z)
    }

    fun getTransformationMatrix(matrix: Matrix4f): Matrix4f {
        matrix.translate(translation)
        if (scheduleRotation) {
            val pivotX = modelOrigin.x.toFloat()*scale.x
            val pivotY = modelOrigin.y.toFloat()*scale.y

            matrix.rotate(rz, zAxis)

            if (modelOrigin.x != 0.0 || modelOrigin.y != 0.0)
                matrix.translate(-pivotX, -pivotY, 0f)
        }
        scheduleRotation = false
        matrix.scale(scale)
        pivot.set(0f, 0f, 0f)
        unpivot.set(0f, 0f, 0f)
        rx = 0f
        ry = 0f
        rz = 0f
        return matrix
    }

    fun rewind() {
        setRotate(0f)
        setTranslate(0f, 0f)
        setDepth(0f)
        setScale(1f, 1f)
        radius = 0.0
        noReset = false
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