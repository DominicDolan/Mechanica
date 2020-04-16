package demo.polygon

import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import input.Cursor
import org.joml.Matrix4f
import util.colors.Color
import util.colors.hex
import util.colors.toColor

class PolygonRenderer2() {

    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                void main(void) {
                    gl_Position = matrices(vec4($position, 1.0));
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val mouse = uniform.vec2(0.0, 0.0)

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                                
                void main(void) {
                    fragColor = $color;
                }
            """

    }

    private val shader = Shader(vertex, fragment)

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    private val transformation = Matrix4f()

    init {
        color = hex(0x00FF0080)
    }

    fun render(model: Model) {
        fragment.mouse.set(Cursor.viewX, Cursor.viewY)
        shader.render(model, transformation)
    }

}