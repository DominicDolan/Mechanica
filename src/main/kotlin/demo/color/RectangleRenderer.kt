package demo.color

import game.Game
import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import gl.utils.createUnitSquareArray
import gl.vbo.AttributeArray
import gl.vbo.pointer.VBOPointer
import input.Mouse
import org.joml.Matrix4f
import util.colors.Color
import util.colors.toColor
import util.extensions.toFloatArray
import util.extensions.vec
import util.units.Vector

class RectangleRenderer {

    private val positions = AttributeArray(createUnitSquareArray().toFloatArray(3), VBOPointer.position)
    val model = Model(positions)
    val transformation: Matrix4f = Matrix4f().identity()

    val vertex = object : ShaderScript() {
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

    private val defaultFragment = object : ShaderScript() {

        val mouse = uniform.vec2(0.0, 0.0)
        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                in vec2 pos;       
                void main(void) {
                    vec2 st = pos - vec2(0.5);
                    st*=2.0;
                    float shade = length(st);
                    float mystery = 1.0 - max($mouse.x, 0.1*pixelScale);
                    float smoothStart = 0.5 - 0.1*pixelScale;
                    float smoothEnd = 0.5;
                    vec2 absolute = st-mystery;
                
                    float offsetXScale = 0.5;
                    float offsetX = offsetXScale*(1.0-mystery);
                
                //                    mystery = $mouse.x;
                    float mystery2 = 1.0/(1.0 - mystery);
                
                    float mysteryY = mystery;
                    float mystery2Y = 1.0/(1.0 - mysteryY+offsetX/offsetXScale);
                
                    vec2 rounded = vec2(max((st.x - mystery - offsetX)*(mystery2), 0.0), max((st.y - mysteryY)*(mystery2Y), 0.0));
                    float alpha = 1.0 - smoothstep(smoothStart, smoothEnd, length( rounded));
                    fragColor = vec4($color.rgb, alpha);
                }
            """

    }

    val fragment = defaultFragment

    val shader: Shader by lazy { Shader(vertex, fragment) }

    var color: Color
        get() = defaultFragment.color.value.toColor()
        set(value) {
            defaultFragment.color.set(value)
        }
    var mouse: Vector
        get() = vec(defaultFragment.mouse.value[0], defaultFragment.mouse.value[1])
        set(value) {
            defaultFragment.mouse.set(value)
        }

    fun render(model: Model = this.model, transformation: Matrix4f = this.transformation) {
//        transformation.translate(-1f, 0f, 0f)
//        transformation.scale(2f, 1f, 1f)
        defaultFragment.mouse.set(Mouse.viewX/Game.view.width + 0.5, Mouse.viewY/ Game.view.height + 0.5)
        shader.render(model, transformation)
        transformation.identity()
    }


}