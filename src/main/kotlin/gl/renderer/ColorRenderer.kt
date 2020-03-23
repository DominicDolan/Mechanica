package gl.renderer

import display.Game
import gl.models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import org.joml.Matrix4f
import util.colors.Color
import util.colors.hex
import util.colors.toColor

internal class ColorRenderer: Renderer() {

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(model, transformation, projection, view)
    }
}