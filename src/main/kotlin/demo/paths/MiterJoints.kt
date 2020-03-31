package demo.paths

import drawer.Drawer
import game.Game
import gl.models.Model
import gl.vbo.AttributeArray
import gl.vbo.ElementIndexArray
import gl.vbo.pointer.AttributePointer
import gl.vbo.pointer.VBOPointer
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import state.State
import util.extensions.vec
import util.units.Vector

fun main() {
    Game.configure {
        setViewport(height = 10.0)
        setStartingState { MiterJoints() }
    }
    Game.run()
}

val angleAttribute = AttributePointer.create(3, 1)

private class MiterJoints : State() {
    val renderer = MiterRenderer()

    private val maxVertices = 100
    private val vbo: AttributeArray
    private val indicesVBO: ElementIndexArray
    private var index = 0
    private var width = 20f
    private val model: Model
    private val transformation = Matrix4f()

    private val path = ArrayList<Vector>()
    private val floats = FloatArray(maxVertices*3)
    private val slopes = FloatArray(maxVertices)
    val angle = Math.toRadians(90.0).toFloat()/2f

    init {

        val indices = ShortArray((maxVertices/4)*6)
        val firstSquareIndices = shortArrayOf(
                0, 1, 2, 1, 3, 2
        )

        vbo = AttributeArray(maxVertices, VBOPointer.position)
        val angleVbo = AttributeArray(maxVertices, angleAttribute)

        for (i in indices.indices step 6) {
            for (j in firstSquareIndices.indices) {
                indices[i+j] = (firstSquareIndices[j] + (i/6)*4).toShort()
            }
        }

        indicesVBO = ElementIndexArray(indices)

        model = Model(vbo, indicesVBO, angleVbo) {
            glDrawElements(GL_TRIANGLES, it.maxVertices, GL_UNSIGNED_SHORT, 0)
        }
        path.add(vec(0, 0))
        path.add(vec(1.3, 0.7))
        path.add(vec(-0.5, 2))
        path.add(vec(1, 3))
        updateFloatsToPath()

        val sb1 = StringBuilder()
        val sb2 = StringBuilder()
        for (i in 0..(4*3*2*2)) {
//            sb1.append("${floats[i]}, ")
            sb2.append("${this.floats[i]}, ")
        }
        println(sb1.toString())
        println(sb2.toString())
        vbo.update(this.floats)
        angleVbo.update(slopes)
    }

    fun updateFloatsToPath() {

        for (p in 0..(path.size-2)) {
            val previous = path[p]
            val current = path[p + 1]

            floats.setVertex(p*2, previous)
            floats.setVertex(p*2 + 1, current)

            val m1 = (current.y - previous.y)/(current.x - previous.x)

            slopes.setAngle(p*2, m1.toFloat())
            slopes.setAngle(p*2 + 1, m1.toFloat())
        }
    }

    fun FloatArray.setAngle(index: Int, angle: Float) {
        this[index*2] = angle
        this[index*2 + 1] = -angle
    }

    fun FloatArray.setVertex(index: Int, vec: Vector) {
        setSingleVertex(index*2, vec)
        setSingleVertex(index*2 + 1, vec)
    }

    fun FloatArray.setSingleVertex(index: Int, vec: Vector) {
        this[index*3] = vec.x.toFloat()
        this[index*3 + 1] = vec.y.toFloat()
    }

    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {
        renderer.render(model, transformation)
    }

}


