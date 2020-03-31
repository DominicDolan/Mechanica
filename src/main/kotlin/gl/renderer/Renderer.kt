package gl.renderer

import game.Game
import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import org.joml.Matrix4f
import util.colors.Color
import util.colors.toColor

open class Renderer {
    var projection: Matrix4f = Game.matrices.projection
    var view: Matrix4f = Game.matrices.view

    protected open val model = Model()
    protected open val transformation: Matrix4f = Matrix4f().identity()

    protected open val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                void main(void) {
                    gl_Position = matrices(vec4($position, 1.0));
                }
                """

    }

    private val defaultFragment = object : ShaderScript() {

        //language=GLSL
        override val main: String = """
                out vec4 fragColor;
                                
                void main(void) {
                    fragColor = $color;
                }
            """

    }

    protected open val fragment = defaultFragment

    protected open val shader: Shader by lazy { Shader(vertex, fragment) }

    open var color: Color
        get() = defaultFragment.color.value.toColor()
        set(value) {
            defaultFragment.color.set(value)
        }

    open fun render(model: Model = this.model, transformation: Matrix4f = this.transformation) {
        shader.render(model, transformation, projection, view)
    }
}