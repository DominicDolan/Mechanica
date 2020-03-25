package gl.renderer

import gl.models.Model
import gl.utils.IndexedVertices
import gl.vbo.AttributeArray
import gl.vbo.ElementIndexArray
import gl.vbo.pointer.VBOPointer
import graphics.Polygon
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import util.extensions.vec

class PolygonRenderer : Renderer() {

    private val shape = listOf(
            vec(0, 0),
            vec(0, 1),
            vec(1, 1.1),
            vec(1.1, 0.5),
            vec(1, 0)
    )

    var polygon: Polygon = Polygon.create(shape)
        set(value) {
            updateBuffers(value.indexedVertices)
            field = value
        }

    private val positionVBO = AttributeArray(100, VBOPointer.position)
    private val indices = ElementIndexArray(200)

    override val model: Model = Model(positionVBO, indices) {
        GL11.glDrawElements(GL11.GL_TRIANGLES, it.maxVertices, GL11.GL_UNSIGNED_SHORT, 0)
    }


    init {
        updateBuffers(polygon.indexedVertices)
    }

    override fun render(model: Model, transformation: Matrix4f) {
        shader.render(this.model, transformation, projection, view)
    }

    private fun updateBuffers(vertices: IndexedVertices) {
        val v = vertices.vertices
        val i = vertices.indices

        positionVBO.update(v)
        indices.update(i)
    }
}