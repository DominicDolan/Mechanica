package com.mechanica.engine.drawer.shader

import com.mechanica.engine.context.loader.MechanicaLoader
import com.mechanica.engine.game.Game
import com.mechanica.engine.game.view.GameMatrices
import com.mechanica.engine.matrix.calculatePixelSize
import com.mechanica.engine.shaders.context.ShaderCreator
import com.mechanica.engine.shaders.draw.GLDraw
import com.mechanica.engine.shaders.models.Model
import com.mechanica.engine.shaders.script.Shader
import com.mechanica.engine.shaders.script.ShaderScript
import org.joml.Matrix4f

abstract class DrawerShader : Shader() {

    abstract override val vertex: DrawerScript
    abstract override val fragment: DrawerScript
    override val tessellation: DrawerScript? = null
    override val geometry: DrawerScript? = null

    override val loader: ShaderCreator by lazy {
        matrixLoaders
        pixelSize
        MechanicaLoader.shaderLoader.createShaderCreator(vertex, fragment, tessellation, geometry)
    }

    private val matrixLoaders by lazy {
        val loaders = ArrayList<MatrixLoader>()
        loaders.add(MatrixLoader(vertex))

        val geometry = this.geometry
        if (geometry != null) {
            loaders.add(MatrixLoader(geometry))
        }

        loaders
    }

    private val pixelSize by lazy { fragment.uniform.float("pixelSize") }

    fun render(model: Model, transformation: Matrix4f, projection: Matrix4f? = null, view: Matrix4f? = null) {

        loadMatrixUniforms(transformation,
                projection ?: Game.matrices.projection,
                view ?: Game.matrices.worldCamera
        )

        render(model)
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
                if (view === Game.matrices.worldCamera) {
                    loader.matrixType.value = 0f
                    loader.pvMatrix.set((Game.matrices as GameMatrices).pvMatrix)
                    this.pixelSize.value = (Game.matrices as GameMatrices).pixelScale
                } else if (view === Game.matrices.uiCamera) {
                    loader.matrixType.value = 0f
                    loader.pvMatrix.set((Game.matrices as GameMatrices).pvUiMatrix)
                    this.pixelSize.value = (Game.matrices as GameMatrices).pixelUIScale
                }
            }
            if (loader.matrixType.value == 1f) {
                this.pixelSize.value = calculatePixelSize(projection, view, Game.surface.height)
            }
        }
    }

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

    companion object {
        fun create(vertex: DrawerScript,
                   fragment: DrawerScript,
                   tessellation: DrawerScript? = null,
                   geometry: DrawerScript? = null): DrawerShader {
            return object : DrawerShader() {
                override val vertex: DrawerScript = vertex
                override val fragment: DrawerScript = fragment
                override val tessellation: DrawerScript? = tessellation
                override val geometry: DrawerScript? = geometry
            }
        }

        fun create(vertex: DrawerScript,
                   fragment: DrawerScript,
                   tessellation: DrawerScript? = null,
                   geometry: DrawerScript? = null, draw: GLDraw.(model: Model) -> Unit): DrawerShader {
            return object : DrawerShader() {
                override val vertex: DrawerScript = vertex
                override val fragment: DrawerScript = fragment
                override val tessellation: DrawerScript? = tessellation
                override val geometry: DrawerScript? = geometry

                override val defaultDraw = draw
            }
        }
    }
}