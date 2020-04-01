package gl.shader

import debug.DebugDrawer
import game.Game
import game.view.GameMatrices
import gl.script.ShaderScript
import org.joml.Matrix4f
import org.joml.Vector3f

internal class ShaderImpl(
        override val vertex: ShaderScript,
        override val fragment: ShaderScript,
        override val tessellation: ShaderScript?,
        override val geometry: ShaderScript?): Shader {

    private val matrixLoaders = ArrayList<MatrixLoader>()

    private val pixelScale = fragment.qualifier("uniform").float("pixelScale")
    
    private val vec3 = Vector3f()

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

        val scale = getScale(transformation)*getScale(projection)*getScale(view)
        this.pixelScale.value = 1f/scale
//        println("Scale: ${1f/scale}")
    }

    private fun setGameMatrices(projection: Matrix4f, view: Matrix4f) {

        for (loader in matrixLoaders) {
            loader.matrixType.value = 1f
            if (projection === Game.matrices.projection) {
                if (view === Game.matrices.view) {
                    loader.matrixType.value = 0f
                    loader.pvMatrix.set((Game.matrices as GameMatrices).pvMatrix)
                } else if (view === Game.matrices.uiView) {
                    loader.matrixType.value = 0f
                    loader.pvMatrix.set((Game.matrices as GameMatrices).pvUiMatrix)
                }
            }
        }
    }

    private fun getScale(mat: Matrix4f): Float {
        mat.getScale(vec3)
        vec3.z = 0f
        return vec3.length()
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