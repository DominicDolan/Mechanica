package gl.renderer

import display.Game
import models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import org.joml.Matrix4f

class ImageRenderer : Renderer {
    override var view: Matrix4f = Game.viewMatrix.get()

    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                out vec2 tc;
                layout (binding=0) uniform sampler2D samp;

                void main(void) {
                    gl_Position = matrices(vec4(position, 1.0));
                    tc = textureCoords;
                }
                """

    }

    private val fragment = object : ShaderScript() {
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

    var alpha: Float
        get() = fragment.alpha.value
        set(value) {
            fragment.alpha.value = value
        }

    private val shader: Shader = Shader(vertex, fragment)

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(model, transformation, projection, view)
    }
}