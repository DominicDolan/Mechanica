package renderer

import models.Model
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

/**
 * Created by domin on 27/10/2017.
 */

var activeVBOs: Int = 0

fun render(model: Model){
    prepareVertexArrays(model.vaoID, 1)


    GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount,
            GL11.GL_UNSIGNED_SHORT, 0)

    disableVertexArrays()
}


fun enableAlphaBlending() {
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
}


fun drawTexture(model: Model) {
    glBindTexture(GL_TEXTURE_2D, model.texture)
    glDrawElements(GL_TRIANGLES, model.vertexCount,
            GL_UNSIGNED_SHORT, 0)
}

fun defaultPrepare(vaoID: Int) {
    GL30.glBindVertexArray(vaoID)
    GL20.glEnableVertexAttribArray(0)
    GL20.glEnableVertexAttribArray(1)
}

fun defaultDraw(model: Model, shader: ShaderProgram) {
    GL11.glEnable(GL11.GL_BLEND)
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

    GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.texture)
    GL11.glDrawElements(GL11.GL_TRIANGLES, model.vertexCount,
            GL11.GL_UNSIGNED_SHORT, 0)

}

//    protected abstract void defaultDraw(TexturedModel model);

fun defaultEnd() {
    GL20.glDisableVertexAttribArray(0)
    GL20.glDisableVertexAttribArray(1)

    GL30.glBindVertexArray(0)
}

fun renderTexture(model: Model){
    prepareVertexArrays(model.vaoID, 1)

    glBindTexture(GL_TEXTURE_2D, model.texture)
    glDrawElements(GL_TRIANGLES, model.vertexCount,
            GL_UNSIGNED_SHORT, 0)

    disableVertexArrays()
}

fun prepareVertexArrays(vaoID: Int, vboCount: Int) {
    GL30.glBindVertexArray(vaoID)
    activeVBOs = vboCount
    for (i in 0 until vboCount) {
        GL20.glEnableVertexAttribArray(i)
    }
}


fun disableVertexArrays() {
    for (i in 0 until activeVBOs) {
        GL20.glDisableVertexAttribArray(i)
    }
    activeVBOs = 0

    GL30.glBindVertexArray(0)
}