package demo.polygon

import drawer.shader.DrawerScript
import drawer.shader.DrawerShader
import gl.models.Model
import gl.shader.Shader
import input.Mouse
import org.joml.Matrix4f
import util.colors.Color
import util.colors.hex
import util.colors.toColor

class PolygonRenderer() {

    private val vertex = object : DrawerScript() {
        //language=GLSL
        override val main: String =
                """
                void main(void) {
                    gl_Position = matrices(vec4($position, 1.0));
                }
                """

    }

    private val fragment = object : DrawerScript() {

        val mouse = uniform.vec2(0.0, 0.0)

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                                
                void main(void) {
                    fragColor = $color;
                }
            """

    }

    private val shader = DrawerShader(vertex, fragment)

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
        fragment.mouse.set(Mouse.view.x, Mouse.view.y)
        shader.render(model, transformation)
    }

}