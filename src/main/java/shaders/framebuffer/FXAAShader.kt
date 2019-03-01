package shaders.framebuffer

import renderer.ShaderProgram

class FXAAShader : ShaderProgram("framebuffer/fxaaVertex.glsl", "framebuffer/fxaaFragment.glsl") {

    var inverseRes = 0

    override fun getAllUniformLocations() {
        inverseRes = getUniformLocation("resinv")
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
    }

}
