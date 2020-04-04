package gl.models

import gl.vbo.Bindable
import gl.vbo.VBO
import graphics.Image
import org.lwjgl.opengl.GL11

open class Model(vararg inputs: Bindable,
                 draw: ((Model) -> Unit)? = null) : Iterable<Bindable> {
    protected val inputs: Array<Bindable> = arrayOf(*inputs)
    val maxVertices: Int
        get() {
            var max = 0
            for (vbo in inputs) {
                if (vbo is VBO<*> && vbo.vertexCount > max) {
                    max = vbo.vertexCount
                }
            }
            return max
        }
    var vertexCount = maxVertices
    val draw = draw?: {
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, it.vertexCount)
    }

    fun bind() {
        for (vbo in inputs) {
            vbo.bind()
        }
    }

    override fun iterator() = inputs.iterator()

}