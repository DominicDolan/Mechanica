package gl.shader

import game.Game
import game.view.GameMatrices
import game.view.Matrices
import gl.script.ShaderScript
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.min

internal class ShaderImpl(
        override val vertex: ShaderScript,
        override val fragment: ShaderScript,
        override val tessellation: ShaderScript?,
        override val geometry: ShaderScript?): Shader() {

    private val matrixLoaders = ArrayList<MatrixLoader>()

    private val loader: ShaderLoader by lazy { ShaderLoader(vertex, fragment, tessellation, geometry) }

    override val id: Int
        get() = loader.id

    override fun loadMatrices(transformation: Matrix4f, projection: Matrix4f?, view: Matrix4f?) {
        loadMatrixUniforms(transformation,
                projection ?: Game.matrices.projection,
                view ?: Game.matrices.view
        )
    }

    private fun loadMatrixUniforms(transformation: Matrix4f, projection: Matrix4f, view: Matrix4f) {
        for (loader in matrixLoaders) {
            loader.transformation.set(transformation)
            loader.projection.set(projection)
            loader.view.set(view)
        }
    }


}