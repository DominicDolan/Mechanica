package demo.polygon

import drawer.shader.DrawerScript
import game.Game
import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import gl.vbo.VBO
import input.Cursor
import org.joml.Matrix4f
import util.extensions.toFloatArray
import util.extensions.vec
import util.units.Vector

class PolygonRenderer {
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
    private val vertex = object : DrawerScript() {
        //language=GLSL
        override val main: String =
                """
                out vec2 pos;
                void main(void) {
                    pos = $position.xy;
                    gl_Position = matrices(vec4($position, 1.0));
                }
                """

    }

    private val fragment = object : DrawerScript() {

        val mouse = uniform.vec2(0.0, 0.0)
        val array = uniform.type("float", "p[22]", points.toFloatArray(2))

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                in vec2 pos;
                                
                void main(void) {
                                        
                    bool result = false;
                    vec2 prev = vec2(p[p.length() - 2], p[p.length() - 1]);
                    
                    for (int i=0; i<p.length()-1;i+=2) {
                        vec2 vec = vec2(p[i], p[i + 1]);
                        if ((vec.y > pos.y) != (prev.y > pos.y) &&
                                (pos.x < (prev.x - vec.x) * (pos.y - vec.y) / (prev.y-vec.y) + vec.x)) {
                            result = !result;
                        }
                        prev = vec;
                    }
                    
                    if (result) {
                        fragColor = vec4(0.0, 1.0, 0.0, 1.0);
                    } else {
                        fragColor = vec4(0.3, 0.3, 0.3, 1.0);
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
        println("Points size: ${points.size}")
        println("Floats size: ${points.toFloatArray(2).size}")


        val vbo = VBO.createUnitSquarePositionAttribute()
        model = Model(vbo)
    }

    fun render() {
        fragment.mouse.set(Cursor.viewX, Cursor.viewY)
        shader.render(this.model, transformation)
    }
}