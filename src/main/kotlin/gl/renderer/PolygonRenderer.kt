package gl.renderer

import display.Game
import models.Model
import gl.script.ShaderScript
import gl.shader.Shader
import gl.utils.IndexedVertices
import gl.vbo.AttributeArray
import gl.vbo.ElementIndexArray
import gl.vbo.pointer.VBOPointer
import graphics.Polygon
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import util.colors.Color
import util.colors.hex
import util.colors.toColor
import util.extensions.vec

class PolygonRenderer : Renderer {
    override var view: Matrix4f = Game.viewMatrix.get()

    private val vertex = object : ShaderScript() {
        //language=GLSL
        override val main: String =
                """
                void main(void) {
                    gl_Position = matrices(vec4($position, 1.0));
                }
                """

    }

    private val fragment = object : ShaderScript() {

        val color = uniform.vec4(hex(0xFF00FFFF))

        //language=GLSL
        override val main: String = """
                out vec4 out_Color;
                                
                void main(void) {
                    out_Color = $color;
                }
            """

    }

    private val shader: Shader = Shader(vertex, fragment)

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

    private val model: Model = Model(positionVBO, indices) {
        GL11.glDrawElements(GL11.GL_TRIANGLES, it.maxVertices, GL11.GL_UNSIGNED_SHORT, 0)
    }

    var color: Color
        get() = fragment.color.value.toColor()
        set(value) {
            fragment.color.set(value)
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