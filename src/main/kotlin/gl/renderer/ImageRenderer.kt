package gl.renderer

import gl.Drawable
import gl.script.ShaderScript
import gl.shader.Shader
import org.joml.Matrix4f
import util.colors.hex

class ImageRenderer : Renderer {
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

        val color = uniform.vec4(hex(0xFF00FFFF))

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

    override fun render(drawable: Drawable, transformation: Matrix4f) {
        shader.render(drawable, transformation)
    }
}