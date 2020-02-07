package shader

import models.Model
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30
import shader.script.ShaderScript

class Shader(private val vertex: ShaderScript, private val fragment: ShaderScript) {
    private var loader: ShaderLoader? = null

    private var activeVBOs = 0

    var drawProcedure = defaultDrawProcedure

    fun render(model: Model) {
        prepareVertexArrays(model.vaoID, 2)
        start()

        loadUniforms()

        enableAlphaBlending()

        drawProcedure(model)

        stop()
        disableVertexArrays()
    }

    private fun start() {
        val loader = this.loader ?: ShaderLoader(vertex, fragment)
        GL20.glUseProgram(loader.id)
    }

    private fun stop() {
        GL20.glUseProgram(0)
    }

    private fun loadUniforms() {
        vertex.loadUniforms()
        fragment.loadUniforms()
    }

    private fun prepareVertexArrays(vaoID: Int, vboCount: Int) {
        GL30.glBindVertexArray(vaoID)
        activeVBOs = vboCount
        for (i in 0 until vboCount) {
            GL20.glEnableVertexAttribArray(i)
        }
    }

    private fun disableVertexArrays() {
        for (i in 0 until activeVBOs) {
            GL20.glDisableVertexAttribArray(i)
        }
        activeVBOs = 0

        GL30.glBindVertexArray(0)
    }

    private fun enableAlphaBlending() {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    }

    companion object {
        val defaultDrawProcedure: (Model) -> Unit = {
            if (it.drawType == GL11.GL_TRIANGLES) {
                GL11.glDrawElements(GL11.GL_TRIANGLES, it.vertexCount,
                        GL11.GL_UNSIGNED_SHORT, 0)
            } else
                GL11.glDrawArrays(it.drawType, 0,
                        it.vertexCount)

        }
    }
}