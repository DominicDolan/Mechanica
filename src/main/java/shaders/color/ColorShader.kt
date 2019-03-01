package shaders.color

import display.Game
import renderer.WorldShader

/**
 * Created by domin on 28/10/2017.
 */
class ColorShader : WorldShader("color/colorVertex.glsl", "color/colorFragment.glsl") {

    var color: Int = 0

    init {
        start()
        loadProjectionMatrix(Game.projectionMatrix.create())
        loadViewMatrix(Game.viewMatrix.create())
        stop()
    }

    override fun getAllUniformLocations() {
        getMatrixLocations()
        color = super.getUniformLocation("color")
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoords")

    }
}