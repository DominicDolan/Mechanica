package gl.renderer

import gl.Drawable
import org.joml.Matrix4f

internal interface Renderer {
    fun render(drawable: Drawable, transformation: Matrix4f)
}