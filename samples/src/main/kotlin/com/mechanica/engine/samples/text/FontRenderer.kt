package com.mechanica.engine.samples.text

import com.mechanica.engine.color.Color
import com.mechanica.engine.color.toColor
import com.mechanica.engine.debug.ScreenLog
import com.mechanica.engine.drawer.shader.DrawerScript
import com.mechanica.engine.drawer.shader.DrawerShader
import com.mechanica.engine.game.Game
import com.mechanica.engine.input.mouse.Mouse
import com.mechanica.engine.models.TextModel
import com.mechanica.engine.resources.Res
import com.mechanica.engine.text.Font
import com.mechanica.engine.unit.vector.DynamicVector
import com.mechanica.engine.unit.vector.Vector
import com.mechanica.engine.unit.vector.vec
import com.mechanica.engine.util.extensions.constrain
import org.joml.Matrix4f
import kotlin.math.ceil
import kotlin.math.max


class FontRenderer {

    private val transformation = Matrix4f().identity()
    private val projection = Game.matrices.projection
    private val view = Game.matrices.view

    private val vertex = object : DrawerScript() {
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

    private val fragment = object : DrawerScript() {

        val mouse = uniform.float()
        //language=GLSL
        override val main: String = """
                sample in vec2 tc;
                layout (binding=0) uniform sampler2D samp;
                out vec4 out_Color;
                                
                void main(void) {
                    vec4 texColor = texture(samp, tc);
                    float alpha = texColor.a;
                    float thickness = 0.52;
                    vec3 neon = vec3(1.0, 0.43, 0.78);
                    
                    vec4 inside = vec4(mix(neon, vec3(1.0), 0.95), 1.0);
                    vec4 outside = vec4(mix(vec3(1.0, 0.43, 0.78), vec3(1.0), alpha*thickness), texColor.a/0.55 - 0.5);
                    float border = 0.03;
                    if (alpha < 1.0 && alpha > thickness) {
                        out_Color = inside;
                    } else if (alpha < thickness && alpha > thickness-border) {
                        float blend = (alpha - thickness + border)/border;
                        out_Color = mix(outside, inside, blend);
                    } else {
                        float blend = texColor.a*thickness;
                        out_Color = outside;
                    }
//                    out_Color = vec4($color.rgb, alpha*$color.a);
                }
            """

    }

    private val shader = DrawerShader.create(vertex, fragment)

    var text: String = ""
        set(value) {
            model.string = value
            field = value
        }

    private val font: Font = Font.create(Res.font["Roboto-Regular.ttf"]) {
        characterSize = 100f
        configureSDF {
            start = -20.0
            end = 20.0
        }
    }

    val model: TextModel = TextModel(text, font)

    private val characterOutput = CharacterOutputImpl()

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    var fontSize: Double = 1.0

    var position: DynamicVector = DynamicVector.create()
        set(value) {
            field.set(value)
        }

    fun render(transformation: Matrix4f ) {
        fragment.mouse.value = (Mouse.world.x/Game.view.width).toFloat() + 0.5f
        if (transformation == this.transformation) {
            transformation.translate(position.x.toFloat(), position.y.toFloat(), 0f)
            transformation.scale(fontSize.toFloat(), fontSize.toFloat(), 1f)
        }
        shader.render(this.model, transformation, projection, view)
        if (transformation == this.transformation) { transformation.identity() }
        ScreenLog { "testing testing" }
    }

    fun from(index: Int): CharacterOutput {
        characterOutput.inputIndex = index
        return characterOutput
    }

    fun from(location: Vector): CharacterOutput {
        characterOutput.inputPosition = location
        return characterOutput
    }

    fun from(x: Double, y: Double): CharacterOutput {
        characterOutput.x = x
        characterOutput.y = y
        return characterOutput
    }

    private inner class CharacterOutputImpl : CharacterOutput() {

        private val INPUT_INDEX = 0
        private val INPUT_POSITION = 1

        private var inputType = 0

        var inputIndex = 0
            set(value) {
                inputType = INPUT_INDEX
                field = value
            }

        var x: Double
            get() = inputPosition.x
            set(value) {
                inputType = INPUT_POSITION
                (inputPosition as DynamicVector).x = value
            }
        var y: Double
            get() = inputPosition.y
            set(value) {
                inputType = INPUT_POSITION
                (inputPosition as DynamicVector).y = value
            }

        var inputPosition: Vector = DynamicVector.create()
            set(value) {
                inputType = INPUT_POSITION
                (field as DynamicVector).set(value)
            }

        override fun getPosition(): Vector {
            return when (inputType) {
                INPUT_INDEX -> {
                    getCharacterPosition(inputIndex)
                }
                INPUT_POSITION -> {
                    getClosestCharacterPosition(inputPosition.x, inputPosition.y)
                }
                else -> vec(0.0, 0.0)
            }
        }

        override fun getIndex(): Int {
            return when (inputType) {
                INPUT_INDEX -> {
                    inputIndex
                }
                INPUT_POSITION -> {
                    getCharacterIndex(inputPosition)
                }
                else -> inputIndex
            }
        }

        fun getCharacterPosition(index: Int): Vector {
            val safeIndex = max(0, index)
            val x = model.getCharacterPosition(safeIndex)*fontSize + this@FontRenderer.position.x
            val y = -model.getLine(safeIndex)*fontSize + this@FontRenderer.position.y
//            println(" Calculated x: $x, font size: ${fontSize}, position x: ${this@FontRenderer.position.x}")
            return vec(x, y)
        }

        fun getCharacterIndex(x: Double, y: Double): Int {
            val textPosition = this@FontRenderer.position
            val adjustedY = (y - textPosition.y)/fontSize
            val adjustedX = (x - textPosition.x)/fontSize

            val restrained = (-adjustedY).constrain(0.0, model.lineCount.toDouble())
            val line = ceil(restrained).toInt()

            return model.getCharacterIndex(adjustedX, line)
        }

        fun getClosestCharacterPosition(x: Double, y: Double): Vector {
            val textPosition = this@FontRenderer.position
            val adjustedY = (y - textPosition.y)/fontSize
            val adjustedX = (x - textPosition.x)/fontSize

            val line = (-adjustedY).constrain(0.0, model.lineCount.toDouble())
            println("calculated line: $line")
            val ceil = ceil(line)

            val xOut = model.getClosestCharacterPosition(adjustedX, ceil.toInt())*fontSize + textPosition.x
            val yOut = -ceil*fontSize + textPosition.y
            return vec(xOut, yOut)
        }

        fun getCharacterIndex(coordinates: Vector) = getCharacterIndex(coordinates.x, coordinates.y)
    }

    abstract class CharacterOutput {
        abstract fun getPosition(): Vector
        abstract fun getIndex(): Int
    }


}