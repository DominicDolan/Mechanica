package shaders.texture

import display.Game
import renderer.WorldShader


/**
 * Created by domin on 15 Apr 2017.
 */
class TextureShader : WorldShader("texture/textureVertex.glsl", "texture/textureFragment.glsl") {

    init {
        start()
        loadProjectionMatrix(Game.projectionMatrix.create())
        loadViewMatrix(Game.viewMatrix.create())
        stop()
    }

    override fun getAllUniformLocations() {
        getMatrixLocations()
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoords")

    }



}
