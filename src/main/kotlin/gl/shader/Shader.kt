package gl.shader

import display.Game
import gl.Drawable
import gl.script.Declarations
import gl.script.ShaderScript
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20

interface Shader {
    val vertex: ShaderScript
    val fragment: ShaderScript
    val id: Int

    fun load() {
        GL20.glUseProgram(id)
        loadUniforms()
    }

    fun loadUniforms() {
        vertex.loadUniforms()
        fragment.loadUniforms()
    }

    fun render(drawable: Drawable, transformation: Matrix4f, projection: Matrix4f? = null, view: Matrix4f? = null) {
        val projMatrix = projection ?: Game.projectionMatrix.create()
        val viewMatrix = view ?: Game.viewMatrix.create()

        GL20.glUseProgram(id)

        Declarations.transformation.set(transformation)
        Declarations.projection.set(projMatrix)
        Declarations.view.set(viewMatrix)

        load()

        drawable.bind()
        drawable.draw(drawable)
    }

    companion object {
        operator fun invoke(vertex: ShaderScript, fragment: ShaderScript): Shader {
            return ShaderImpl(vertex, fragment)
        }
    }
}