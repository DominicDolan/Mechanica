package gl.renderer

import display.Game
import models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import org.joml.Matrix4f

class ImageRenderer : Renderer {
    override var view: Matrix4f = Game.viewMatrix.create()

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
        //language=GLSL
        override val main: String = """
                in vec2 tc;
                out vec4 color;
                layout (binding=0) uniform sampler2D samp;
                                
                void main(void) {
                    color = texture(samp, tc);
                }
            """

    }

    private val shader: Shader = Shader(vertex, fragment)

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(model, transformation, projection, view)
    }
}