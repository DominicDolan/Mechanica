package gl.renderer

import models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import org.joml.Matrix4f
import util.colors.Color
import util.colors.hex
import util.colors.toColor

internal class ColorRenderer: Renderer {
    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                void main(void) {
                    gl_Position = matrices(vec4(position, 1.0));
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FFFF))

        //language=GLSL
        override val main: String = """
                out vec4 out_Color;
                                
                void main(void) {
                    out_Color = $color;
                }
            """

    }

    private val shader: Shader = Shader(vertex, fragment)

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(model, transformation)
    }
}