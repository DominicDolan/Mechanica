package demo.paths

import display.Game
import display.GameOptions
import drawer.Drawer
import gl.utils.positionAttribute
import gl.vbo.AttributePointer
import gl.vbo.MutableVBO
import gl.vbo.VBO
import loader.toBuffer
import models.Model
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import state.State
import util.extensions.vec
import util.units.Vector
import kotlin.math.sqrt

fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { MiterJoints() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

val angleAttribute = AttributePointer.create(3, 1)

private class MiterJoints : State() {
    val renderer = MiterRenderer()

    private val maxVertices = 100
    private val vbo: MutableVBO
    private val indicesVBO: VBO
    private var index = 0
    private var width = 20f
    private val model: Model
    private val transformation = Matrix4f()

    private val path = ArrayList<Vector>()
    private val floats = FloatArray(maxVertices*3)
    private val slopes = FloatArray(maxVertices)
    val angle = Math.toRadians(90.0).toFloat()/2f

    init {


        val square = arrayOf(
            vec(0f, 0f),
            vec(1f, 0f),
            vec(0f, 1f),
            vec(1f, 1f)
        )

        val indices = ShortArray((maxVertices/4)*6)
        val firstSquareIndices = shortArrayOf(
                0, 1, 2, 1, 3, 2
        )

        vbo = VBO.createMutable(maxVertices, positionAttribute)
        val angleVbo = VBO.createMutable(maxVertices, angleAttribute)

        for (i in indices.indices step 6) {
            for (j in firstSquareIndices.indices) {
                indices[i+j] = (firstSquareIndices[j] + (i/6)*4).toShort()
            }
        }

        indicesVBO = VBO.createIndicesBuffer(indices.toBuffer())

        model = Model(vbo, indicesVBO, angleVbo){
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
        vbo.updateBuffer(this.floats)
        angleVbo.updateBuffer(slopes)
    }

    fun updateFloatsToPath() {

        for (p in 0..(path.size-2)) {
            val previous = path[p]
            val current = path[p + 1]

            floats.setVertex(p*2, previous)
            floats.setVertex(p*2 + 1, current)

            val m1 = (current.y - previous.y)/(current.x - previous.x)
            println(m1)
            val m2 = if (p + 2 < path.size) {
                val next = path[p+2]
                (next.y - current.y)/(next.x - current.x)
            } else {
                m1
            }

            val m3 = (m1*m2 - sqrt((1+m1*m1)*(1+m2*m2)) - 1)/(m1 + m2)
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


