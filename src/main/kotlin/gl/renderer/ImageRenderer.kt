package gl.renderer

import drawer.shader.DrawerScript
import gl.models.Model
import gl.script.ShaderScript
import org.joml.Matrix4f

class ImageRenderer : Renderer() {

    override val vertex = object : DrawerScript() {
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

    private val _fragment = object : DrawerScript() {
        val alpha = uniform.float(1f)
        //language=GLSL
        override val main: String = """
                in vec2 tc;
                out vec4 color;
                layout (binding=0) uniform sampler2D samp;
                                
                void main(void) {
                    vec4 texColor = texture(samp, tc);
                    color = vec4(texColor.rgb, texColor.a*$alpha);
                }
            """

    }
    override val fragment = _fragment

    var alpha: Float
        get() = _fragment.alpha.value
        set(value) {
            _fragment.alpha.value = value
        }

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(model, transformation, projection, view)
    }
}