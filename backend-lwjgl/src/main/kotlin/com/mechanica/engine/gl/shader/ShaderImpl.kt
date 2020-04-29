package com.mechanica.engine.gl.shader

import com.mechanica.engine.gl.script.ShaderScript
import org.joml.Matrix4f

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
                projection ?: defaultMatrices.projection,
                view ?: defaultMatrices.view
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