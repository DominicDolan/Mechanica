package gl.shader

import display.Game
import gl.Drawable
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import gl.script.Declarations
import gl.script.ShaderScript
import matrices.ProjectionMatrix
import matrices.TransformationMatrix

internal class ShaderImpl(override val vertex: ShaderScript, override val fragment: ShaderScript): Shader {
    private var _loader: ShaderLoader? = null
    private val loader: ShaderLoader
        get() {
            val l = this._loader
            val loader = l ?: ShaderLoader(vertex, fragment)
            this._loader = loader
            return loader
        }
    override val id: Int
        get() = loader.id

}