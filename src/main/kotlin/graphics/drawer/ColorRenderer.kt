package graphics.drawer

import gl.Renderer
import gl.VBO
import gl.script.ShaderScript
import gl.shader.Shader
import org.lwjgl.opengl.GL11
import util.colors.Color
import util.colors.hex
import util.colors.toColor

class ColorRenderer : Renderer() {
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

    override val shader: Shader = Shader(vertex, fragment)

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
        }

    override fun draw(vbo: VBO) {
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vbo.vertexCount)
    }
}