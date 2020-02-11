package shader

import display.Game
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import shader.script.ShaderScript

abstract class Renderer(private val shader: Shader) {

    private var _projection: Matrix4f? = null
            get() = if (field == null) projection else field

    private var _view: Matrix4f? = null
        get() = if (field == null) view else field

    constructor(vertexShader: ShaderScript, fragmentShader: ShaderScript)
            : this(Shader(vertexShader, fragmentShader))

    fun render(vbo: VBO, transformation: Matrix4f) {
        val projection = _projection ?: Matrix4f()
        val view = _view ?: Matrix4f()

        GL20.glUseProgram(shader.id)

        Shader.loadTransformationMatrix(transformation)
        Shader.loadProjectionMatrix(projection)
        Shader.loadViewMatrix(view)

        shader.load()

        enableAlphaBlending()
        vbo.bind()
        draw(vbo)

    }

    protected abstract fun draw(vbo: VBO)

    companion object {
        var projection: Matrix4f = Game.projectionMatrix.create()
            private set
        var view: Matrix4f = Game.viewMatrix.create()
            private set

        internal fun startFrame() {
            projection = Game.projectionMatrix.create()
            view = Game.viewMatrix.create()
        }

        private fun enableAlphaBlending() {
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        }

        val defaultDrawProcedure: (VBO) -> Unit = {
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, it.vertexCount)

        }
    }
}