package demo.polygon

import geometry.PolygonModel
import geometry.triangulation.Triangulator
import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import gl.vbo.AttributeArray
import gl.vbo.ElementIndexArray
import gl.vbo.pointer.AttributePointer
import gl.vbo.pointer.VBOPointer
import input.Cursor
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL40.*
import util.colors.Color
import util.colors.hex
import util.colors.toColor
import util.extensions.fill
import util.extensions.toFloatArray
import util.extensions.vec
import util.units.LightweightVector
import util.units.Vector

class PolygonRenderer2() {

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

        val mouse = uniform.vec2(0.0, 0.0)

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                                
                void main(void) {
                    fragColor = $color;
                }
            """

    }

    private val shader = Shader(vertex, fragment)

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
        fragment.mouse.set(Cursor.viewX, Cursor.viewY)
        shader.render(model, transformation)
    }

}