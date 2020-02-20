package gl.renderer

import models.Model
import org.joml.Matrix4f

internal interface Renderer {
    fun render(model: Model, transformation: Matrix4f)
}