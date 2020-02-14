package gl

import graphics.Image
import org.lwjgl.opengl.GL11

class Drawable(private vararg val vbos: VBO,
               draw: ((Drawable) -> Unit)? = null) : Iterable<VBO> {
    var image = Image(-1)
    var vertexCount = 6
    val draw = draw?: {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, it.image.id)
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, it.vertexCount)
    }

    init {
        vertexCount = vbos[0].vertexCount
    }

    fun bind() {
        for (vbo in vbos) {
            vbo.bind()
        }
    }


    override fun iterator() = vbos.iterator()

    companion object {
        private val defaultDraw = {

        }
    }
}