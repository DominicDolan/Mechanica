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
import util.extensions.toFloatArray
import util.extensions.vec
import util.units.Vector

class PolygonRenderer2 {
    private val model: Model

    val points: Array<Vector> = arrayOf(
            vec(0, 0),
            vec(0.5, 0.1),
            vec(1.0, 0.0),
            vec(0.5, 0.5),
            vec(1.0, 0.7),
            vec(1.0, 1.0),
            vec(0.7, 1.0),
            vec(0.5, 0.7),
            vec(0.0, 1.0),
            vec(0.2, 0.5),
            vec(0.0, 0.0)
    )
    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                out vec2 pos;
                out vec2 index;
                void main(void) {
                    pos = $position.xy;
                    if (pos.x < 0.5) {
                        index.x = -1.0;
                    } else {
                        index.x = 1.0;
                    }
                    if (pos.y < 0.5) {
                        index.y = -1.0;
                    } else {
                        index.y = 1.0;
                    }
                    gl_Position = matrices(vec4($position, 1.0));
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val mouse = uniform.vec2(0.0, 0.0)
        val array = uniform.type("float", "p[22]", points.toFloatArray(2))

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                in vec2 pos;
                in vec2 index;
                                
                void main(void) {
                    
                    fragColor = vec4(index, 0.0, 1.0);
                    if (index.x > 1.0 || index.y > 1.0) {
                        fragColor = vec4(0.0, 0.0, 1.0, 1.0);
                    }
                    
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

    init {

        val vbo = AttributeArray(points.toFloatArray(3), VBOPointer.position)
        model = Model(vbo) {
            glDrawArrays(GL_TRIANGLE_FAN, 0, it.vertexCount)
        }
    }

    fun render(model: Model) {
        fragment.mouse.set(Cursor.viewX, Cursor.viewY)
        shader.render(this.model, transformation)
    }
}