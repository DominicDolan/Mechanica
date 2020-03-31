package demo.polygon

import game.Game
import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import input.Cursor
import org.joml.Matrix4f

class PolygonRenderer {

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

//        val color = uniform.vec4(hex(0xFF00FFFF))
        val mouse = uniform.vec2(0.0, 0.0)
        val time = uniform.float(0f)
        val resolution = uniform.vec2(Game.window.width.toDouble(), Game.window.height.toDouble())

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                                
                void main(void) {
                    vec2 st = gl_FragCoord.xy/$resolution;
                    fragColor = vec4(st.x,st.y, 0.0, 1.0);
                }
            """

    }

    private val shader = Shader(vertex, fragment)
    private val startTime = System.currentTimeMillis()
//    var color: Color
//        get() = fragment.color.value.toColor()
//        set(value) {
//            fragment.color.set(value)
//        }

    private val transformation = Matrix4f()

    fun render(model: Model) {
        fragment.mouse.set(Cursor.viewX, Cursor.viewY)
        fragment.time.value = (System.currentTimeMillis() - startTime).toFloat()/1000f
        shader.render(model, transformation)
    }
}