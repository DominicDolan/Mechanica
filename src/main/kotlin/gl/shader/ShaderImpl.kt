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
        override val geometry: ShaderScript?): Shader {

    private val matrixLoaders = ArrayList<MatrixLoader>()

    private val pixelSize = fragment.qualifier("uniform").float("pixelSize")

    private val loader: ShaderLoader by lazy { ShaderLoader(vertex, fragment, tessellation, geometry) }

    override val id: Int
        get() = loader.id

    init {
        matrixLoaders.add(MatrixLoader(vertex))
        if (geometry != null) {
            matrixLoaders.add(MatrixLoader(geometry))
        }
    }

    override fun loadMatrices(transformation: Matrix4f, projection: Matrix4f?, view: Matrix4f?) {
        loadMatrixUniforms(transformation,
                projection ?: Game.matrices.projection,
                view ?: Game.matrices.view
        )
    }

    private fun loadMatrixUniforms(transformation: Matrix4f, projection: Matrix4f, view: Matrix4f) {
        setGameMatrices(projection, view)

        for (loader in matrixLoaders) {
            loader.transformation.set(transformation)
            loader.projection.set(projection)
            loader.view.set(view)
        }
    }

    private fun setGameMatrices(projection: Matrix4f, view: Matrix4f) {

        for (loader in matrixLoaders) {
            loader.matrixType.value = 1f
            if (projection === Game.matrices.projection) {
                if (view === Game.matrices.view) {
                    loader.matrixType.value = 0f
                    loader.pvMatrix.set((Game.matrices as GameMatrices).pvMatrix)
                    this.pixelSize.value = (Game.matrices as GameMatrices).pixelScale
                } else if (view === Game.matrices.uiView) {
                    loader.matrixType.value = 0f
                    loader.pvMatrix.set((Game.matrices as GameMatrices).pvUiMatrix)
                    this.pixelSize.value = (Game.matrices as GameMatrices).pixelUIScale
                }
            }
            if (loader.matrixType.value == 1f) {
                this.pixelSize.value = Matrices.calculatePixelSize(projection, view)
            }
        }
    }

    private class MatrixLoader(script: ShaderScript) {
        val matrixType = script.qualifier("uniform").float("matrixType")

        val projection = script.qualifier("uniform").mat4("projection")
        val transformation = script.qualifier("uniform").mat4("transformation")
        val view = script.qualifier("uniform").mat4("view")
        val pvMatrix = script.qualifier("uniform").mat4("pvMatrix")

        init {
            loadMatrixMethod(script)
        }

        fun loadMatrixMethod(script: ShaderScript) {
            if (!script.script.contains("vec4 matrices(vec4 position)")) {
                //language=GLSL
                script.addOther("""
                    vec4 matrices(vec4 position) {
                        if(matrixType == 0f) {
                            return pvMatrix*transformation*position;
                        }
                        return projection*view*transformation*position;
                    }
                """)
            }
        }
    }
}