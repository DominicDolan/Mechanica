package demo.paths

import gl.renderer.Renderer
import gl.script.ShaderScript
import gl.shader.Shader
import gl.models.Model
import org.joml.Matrix4f
import util.colors.Color
import util.colors.hex
import util.colors.toColor

class MiterRenderer : Renderer() {

    override val vertex = object : ShaderScript() {

        val angle = attribute(angleAttribute).float()

        //language=GLSL
        override val main: String =
                """
                #define M_PI 3.1415926535897932384626433832795
                uniform float w = 1.0;
                void main(void) {
                    float tangent = atan($angle);
                    float normal = tangent + M_PI/4.0;
                    gl_Position = matrices(vec4(position.x + w*cos(normal), position.y + w*sin(normal), position.z, 1.0));
                }
                """

    }

    private val _fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FFFF))

        //language=GLSL
        override val main: String = """
                out vec4 out_Color;
                                
                void main(void) {
                    out_Color = $color;
                }
            """

    }
    override val fragment = _fragment

    override var color: Color
        get() = _fragment.color.value.toColor()
        set(value) {
            _fragment.color.set(value)
        }

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(model, transformation)
    }


}