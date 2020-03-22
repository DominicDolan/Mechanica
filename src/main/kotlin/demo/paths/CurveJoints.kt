package demo.paths

import display.Game
import display.GameOptions
import drawer.Drawer
import gl.vbo.AttributeArray
import gl.vbo.VBO
import gl.vbo.pointer.VBOPointer
import input.Cursor
import input.Keyboard
import models.Model
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20.*
import state.State
import util.colors.hsl
import util.extensions.degrees
import util.extensions.distanceTo
import util.extensions.vec
import util.units.Vector

fun main() {
    val options = GameOptions()
            .setResolution(1280, 720)
            .setDebugMode(true)
            .setViewPort(height = 10.0)
            .setStartingState { StartMain() }

    Game.start(options)
    Game.update()
    Game.destroy()
}

private class StartMain : State() {
    val renderer = CurveRenderer()

    private val maxVertices = 10000
    private val vbo = AttributeArray(maxVertices, VBOPointer.position)
    private val floats = FloatArray((maxVertices)*3)
    private var index = 0
    private var width = 20f
    private val model = Model(vbo) {
        glClear(GL_STENCIL_BUFFER_BIT)
        glLineWidth(width)
        glPointSize(if (width < 10f) width else 10f)

        glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE)
        glStencilFunc(GL_NOTEQUAL, 1, 0xFF) // all fragments should pass the stencil test
        glStencilMask(0xFF) // enable writing to the stencil buffer

        glDrawArrays(GL_POINTS, 0, index)

        glDrawArrays(GL_LINE_STRIP, 0, index)

        glStencilFunc(GL_ALWAYS, 0, 0xFF) // passes when stencil is 0
    }
    private val transformation = Matrix4f()

    init {
        vbo.update(floats)
        glEnable(GL_STENCIL_TEST)
        val array = FloatArray(2)
        GL11.glGetFloatv(GL_ALIASED_LINE_WIDTH_RANGE, array)
        println(array[0])
        println(array[1])
    }

    override fun update(delta: Double) {
    }

    override fun render(draw: Drawer) {
        val yRatio: Float = (Cursor.viewY/Game.viewHeight).toFloat()
        if (Keyboard.MB1.isDown) {
            val cursor = vec(Cursor.viewX, Cursor.viewY)
            val minLineLength = 0.05
            val nextLineLength = getPoint(index-1).distanceTo(cursor)
            if (nextLineLength > minLineLength) {
                if (index < maxVertices) {
                    floats[index * 3] = cursor.x.toFloat()
                    floats[index * 3 + 1] = cursor.y.toFloat()
                    index++
                }

                vbo.update(floats)
            }
        }

        width = 10f + yRatio*20f

        val color = renderer.color
        draw.red.circle(0, 0, 2)
        renderer.color = hsl((yRatio*360f).degrees, color.saturation, color.lightness, 0.5)
        renderer.render(model, transformation)
        transformation.identity()
    }


    private fun getPoint(index: Int): Vector {
        return when {
            index < 0 -> getPoint(0)
            index > maxVertices -> getPoint(maxVertices)
            else -> vec(floats[index * 3], floats[index * 3 + 1])
        }
    }
}


