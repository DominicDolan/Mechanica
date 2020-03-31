package gl.renderer

import gl.models.Model
import org.joml.Matrix4f

internal class ColorRenderer: Renderer() {

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(model, transformation, projection, view)
    }
}