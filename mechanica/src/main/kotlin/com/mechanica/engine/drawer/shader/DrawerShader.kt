package com.mechanica.engine.drawer.shader

import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.shader.script.ShaderScript
import com.mechanica.engine.shader.script.Shader
import com.mechanica.engine.shader.script.ShaderLoader
import com.mechanica.engine.matrix.Matrices
import com.mechanica.engine.models.Model
import org.joml.Matrix4f
import org.lwjgl.opengl.GL20

class DrawerShader(
        vertex: DrawerScript,
        fragment: DrawerScript,
        tessellation: DrawerScript? = null,
        geometry: DrawerScript? = null): Shader(vertex, fragment, tessellation, geometry) {

    private val matrixLoaders = ArrayList<MatrixLoader>()

    private val pixelSize = fragment.uniform.float("pixelSize")

    private val loader: ShaderLoader by lazy { ShaderLoader(vertex, fragment, tessellation, geometry) }

    override val id: Int
        get() = loader.id

    init {
        matrixLoaders.add(MatrixLoader(vertex))
        if (geometry != null) {
            matrixLoaders.add(MatrixLoader(geometry))
        }
    }

    fun render(model: Model, transformation: Matrix4f, projection: Matrix4f? = null, view: Matrix4f? = null) {

        loadMatrixUniforms(transformation,
                projection ?: Game.matrices.projection,
                view ?: Game.matrices.view
        )
        load()

        model.bind()
        model.draw()
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

    override fun loadProgram(id: Int) {
        GL20.glUseProgram(id)
    }

    override fun loadUniformLocation(name: String) = GL20.glGetUniformLocation(id, name)

    private class MatrixLoader(script: DrawerScript) {
        val matrixType = script.uniform.float("matrixType")

        val projection = script.uniform.mat4("projection")
        val transformation = script.uniform.mat4("transformation")
        val view = script.uniform.mat4("view")

        val pvMatrix = script.uniform.mat4("pvMatrix")

        init {
            loadMatrixMethod(script)
        }

        fun loadMatrixMethod(script: ShaderScript) {
            if (!script.script.contains("vec4 matrices(vec4 position)")) {
                //language=GLSL
                script.addOther("""
                    vec4 matrices(vec4 position) {
                        if(matrixType == 0.0) {
                            return pvMatrix*transformation*position;
                        }
                        return projection*view*transformation*position;
                    }
                """)
            }
        }
    }
}