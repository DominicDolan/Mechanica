package gl.renderer

import display.Game
import models.Model
import org.joml.Matrix4f

internal interface Renderer {
    var projection: Matrix4f
        get() = Game.projectionMatrix.get()
        set(value) {}
    var view: Matrix4f
        get() = Game.viewMatrix.get()
        set(value) {}

    fun render(model: Model, transformation: Matrix4f)
}