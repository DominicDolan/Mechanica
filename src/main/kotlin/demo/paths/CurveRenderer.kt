package demo.paths

import gl.renderer.Renderer
import gl.script.ShaderScript
import gl.shader.Shader
import gl.models.Model
import org.joml.Matrix4f
import util.colors.Color
import util.colors.hex
import util.colors.toColor

class CurveRenderer : Renderer() {

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(model, transformation)
    }


}