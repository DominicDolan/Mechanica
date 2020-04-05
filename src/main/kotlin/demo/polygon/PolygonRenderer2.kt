package demo.polygon

import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import gl.vbo.AttributeArray
import gl.vbo.pointer.VBOPointer
import input.Cursor
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL40.*
import util.extensions.fill
import util.extensions.toFloatArray
import util.extensions.vec
import util.units.LightweightVector
import util.units.Vector

class PolygonRenderer2 {

    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                out vec2 pos;
                out vec2 index;
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
                in vec2 pos;
                in vec2 index;
                                
                void main(void) {
                    
                    fragColor = vec4(0.0, 1.0, 0.0, 1.0);
                    
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

    private var floats: FloatArray

    private val vbo: AttributeArray
    private val model: Model

    init {
        val initialVertices = 300
        floats = FloatArray(initialVertices*3)
        vbo = AttributeArray(initialVertices, VBOPointer.position)

        model = Model(vbo) {
            glDrawArrays(GL_TRIANGLE_FAN, 0, it.vertexCount)
        }
    }

    fun render(path: List<LightweightVector>) {
        fillFloats(path)
        fragment.mouse.set(Cursor.viewX, Cursor.viewY)
        shader.render(this.model, transformation)
    }

    private fun fillFloats(path: List<LightweightVector>) {
        if (path.size*3 >= floats.size) {
            floats = FloatArray(floats.size*2)
        }

        floats.fill(path)
        vbo.update(floats)
    }
}