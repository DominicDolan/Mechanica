package demo.paths

import gl.models.Model
import gl.renderer.Renderer
import org.joml.Matrix4f

class CurveRenderer : Renderer() {

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(model, transformation)
    }


}