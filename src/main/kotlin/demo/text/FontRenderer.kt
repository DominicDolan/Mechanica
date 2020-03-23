package demo.text

import gl.renderer.Renderer
import gl.script.ShaderScript
import gl.shader.Shader
import models.Model
import org.joml.Matrix4f
import resources.Res
import util.colors.Color
import util.colors.hex
import util.colors.toColor
import util.extensions.restrain
import util.extensions.vec
import util.units.MutableVector
import util.units.Vector
import kotlin.math.ceil


class FontRenderer : Renderer {

    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                out vec2 tc;
                layout (binding=0) uniform sampler2D samp;
                
                void main(void) {
                    gl_Position = matrices(vec4($position, 1.0));
                    tc = $textureCoords;
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0x504050FF))

        //language=GLSL
        override val main: String = """
                in vec2 tc;
                layout (binding=0) uniform sampler2D samp;
                out vec4 out_Color;
                                
                void main(void) {
                    vec4 texColor = texture(samp, tc);
                    out_Color = vec4($color.rgb, texColor.a*$color.a);
                }
            """

    }

    private val shader = Shader(vertex, fragment)
    private val fontMap = HashMap<Font, TextModel>()

    val model: TextModel
        get() = fontMap[font] ?: TextModel(font).also { fontMap[font] = it }
    private val transformation = Matrix4f()

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    var text: String = ""
        set(value) {
            model.text = value
            field = value
        }

    var font: Font = Font(Res.font["Roboto-Regular.ttf"]).also { fontMap[it] = TextModel(it) }
        set(value) {
            val newModel = TextModel(value)
            newModel.text = text
            field = value
        }

    var fontSize: Double = 1.0
        set(value) {
            field = value
            transformation.scale(fontSize.toFloat(), fontSize.toFloat(), 1f)
        }

    var position: Vector = MutableVector(0.0, 0.0)
        set(value) {
            (field as MutableVector).set(value)
        }


    override fun render(model: Model, transformation: Matrix4f) {
        this.model.text = text
        shader.render(this.model, this.transformation)
        transformation.identity()
    }

    fun getCharacterPosition(index: Int): Vector {
        val x = model.getCharacterPosition(index)*fontSize + position.x
        val y = -model.getLine(index)*fontSize + position.y
        return vec(x, y)
    }

    fun getCharacterIndex(x: Double, y: Double): Int {

        return 0
    }

    fun getClosestCharacterPosition(x: Double, y: Double): Vector {
        val line = (-y).restrain(0.0, model.newLineCount.toDouble())
        val ceil = ceil(line)
        val xOut = model.getCharacterPosition(x, ceil.toInt())*fontSize + position.x
        val yOut = -ceil*fontSize + position.y
        return vec(xOut, yOut)
    }

    fun getCharacterIndex(coordinates: Vector) = getCharacterIndex(coordinates.x, coordinates.y)

}