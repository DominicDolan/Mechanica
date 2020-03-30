package demo.display

import display.*
import game.Game2
import gl.models.Model
import gl.renderer.Renderer
import gl.script.ShaderScript
import gl.utils.GLContext
import gl.utils.createUnitSquareArray
import gl.utils.startFrame
import gl.vbo.AttributeArray
import gl.vbo.pointer.VBOPointer
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL40
import resources.Res
import util.Timer
import util.colors.hex
import util.extensions.toFloatArray
import kotlin.math.floor

fun main() {
    Game2.configure {
        setViewport(height = 10.0)
    }
    GL40.glClearColor(1f, 1f, 0f, 1f)
    val renderer = DisplayTestRenderer()

    var z = 0f
    Game2.run {
        renderer.color = hex(0xFF0000FF)
        renderer.render()

        if ((floor(Timer.now*10.0)/10.0)%3.0 == 0.0) {
//            Game2.view.height += 1.0
            z -= 0.2f
        }
//        println(Game2.view.height)
    }
}


class DisplayTestRenderer : Renderer() {

    val positionAttribute = AttributeArray(createUnitSquareArray().toFloatArray(3), VBOPointer.position)
    override val model = Model(positionAttribute)

    override val transformation = Matrix4f().identity()

    override val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                void main(void) {
                    gl_Position = projection*view*transformation*(vec4($position, 1.0));
                }
                """

    }
    override val fragment: ShaderScript
        get() = super.fragment

    init {
        transformation.scale(0.5f, 0.5f, 1.0f)
    }

    override fun render(model: Model, transformation: Matrix4f) {
        val view = Game2.matrices.view.get()
        shader.render(this.model, this.transformation, Game2.matrices.projection.get(), view)
    }
}