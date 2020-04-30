package com.mechanica.engine.drawer.shader

import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.gl.script.ShaderScript
import com.mechanica.engine.gl.shader.Shader
import com.mechanica.engine.gl.shader.ShaderLoader
import com.mechanica.engine.matrix.Matrices
import org.joml.Matrix4f

class DrawerShader(
        override val vertex: DrawerScript,
        override val fragment: DrawerScript,
        override val tessellation: DrawerScript? = null,
        override val geometry: DrawerScript? = null): Shader() {

    private val matrixLoaders = ArrayList<DrawerMatrixLoader>()

    private val pixelSize = fragment.qualifier("uniform").float("pixelSize")

    private val loader: ShaderLoader by lazy { ShaderLoader(vertex, fragment, tessellation, geometry) }

    override val id: Int
        get() = loader.id

    init {
        matrixLoaders.add(DrawerMatrixLoader(vertex))
        if (geometry != null) {
            matrixLoaders.add(DrawerMatrixLoader(geometry))
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
                this.pixelSize.value = Matrices.calculatePixelSize(projection, view, Game.window.height)
            }
        }
    }

    private class DrawerMatrixLoader(script: DrawerScript) : MatrixLoader(script) {
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