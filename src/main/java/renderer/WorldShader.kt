package renderer

import org.joml.Matrix4f

/**
 * Created by domin on 15 Apr 2017.
 */
abstract class WorldShader(vertexFile: String, fragmentFile: String) : ShaderProgram(vertexFile, fragmentFile) {

    protected var transformationMatrix: Int = 0
    protected var projectionMatrix: Int = 0
    protected var viewMatrix: Int = 0

    fun getMatrixLocations() {
        transformationMatrix = super.getUniformLocation("transformationMatrix")
        projectionMatrix = super.getUniformLocation("projectionMatrix")
        viewMatrix = super.getUniformLocation("viewMatrix")
    }

    fun loadTransformationMatrix(matrix: Matrix4f) {
        super.loadMatrix(transformationMatrix, matrix)
    }

    fun loadProjectionMatrix(projection: Matrix4f) {
        super.loadMatrix(projectionMatrix, projection)
    }

    fun loadViewMatrix(view: Matrix4f) {
        super.loadMatrix(viewMatrix, view)
    }

    companion object {

        protected val path: String
            get() = WorldShader::class.java.protectionDomain.codeSource.location.path
    }


}
