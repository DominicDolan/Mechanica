package demo.polygon

import gl.script.ShaderScript
import gl.shader.Shader
import models.Model
import org.joml.Matrix4f
import util.colors.Color
import util.colors.hex
import util.colors.toColor

class PolygonRenderer {

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

//        val color = uniform.vec4(hex(0xFF00FFFF))

        //language=GLSL
        override val main: String = """
                out vec4 out_Color;
                                
                void main(void) {
                    out_Color = vec4(gl_FragCoord.xy, 1.0, 1.0);
                }
            """

    }

    private val shader = Shader(vertex, fragment)

//    var color: Color
//        get() = fragment.color.value.toColor()
//        set(value) {
//            fragment.color.set(value)
//        }

    private val transformation = Matrix4f()

    fun render(model: Model) {
        shader.render(model, transformation)
    }
}