package gl.renderer

import display.Game
import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import gl.utils.createUnitSquareArray
import gl.vbo.AttributeArray
import gl.vbo.pointer.VBOPointer
import org.joml.Matrix4f
import util.colors.Color
import util.colors.hex
import util.colors.toColor
import util.extensions.toFloatArray

open class Renderer {
    val projection: Matrix4f
        get() = Game.projectionMatrix.get()
    var view: Matrix4f = Game.viewMatrix.get()

    private val positionVBO = AttributeArray(createUnitSquareArray().toFloatArray(3), VBOPointer.position)
    protected open val model = Model(positionVBO)
    protected open val transformation: Matrix4f = Matrix4f().identity()

    protected open val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                void main(void) {
                    gl_Position = transformation*vec4($position, 1.0);
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

    init {

    }

    open fun render(model: Model = this.model, transformation: Matrix4f = this.transformation) {
        shader.render(model, transformation, projection, view)
    }
}