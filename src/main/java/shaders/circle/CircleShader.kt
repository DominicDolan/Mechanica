package shaders.circle

import display.Game
import renderer.WorldShader

/**
 * Created by domin on 07/11/2017.
 */
class CircleShader : WorldShader("circle/circleVertex.glsl", "circle/circleFragment.glsl") {
    var color: Int = 0
    var strokeWidth: Int = 0
    var border = 0

    init {
        start()
        loadProjectionMatrix(Game.projectionMatrix.create())
        loadViewMatrix(Game.viewMatrix.create())
        stop()
    }

    override fun getAllUniformLocations() {
        getMatrixLocations()
        color = super.getUniformLocation("color")
        strokeWidth = super.getUniformLocation("strokeWidth")
        border = super.getUniformLocation("border")
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoords")

    }
}