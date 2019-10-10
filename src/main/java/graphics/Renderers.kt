package graphics

import display.Game
import font.GUIText
import loader.loadTexture
import loader.loadTexturedQuad
import matrices.TransformationMatrix
import models.Model
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture
import graphics.drawer.Drawer
import graphics.framebuffer.Fbo
import resources.Res
import shaders.circle.CircleShader
import shaders.color.ColorShader
import shaders.font.FontShader
import shaders.framebuffer.FXAAShader
import shaders.texture.TextureShader

/**
 * Created by domin on 27/10/2017.
 */


val transformationMatrix = TransformationMatrix()
var drawingViewMatrix =  Game.viewMatrix

var textureShader = TextureShader()
var textureRenderer:(Drawer.Companion)-> Unit = {
    prepareVertexArrays(it.model.vaoID, 2)

    textureShader.start()
    textureShader.loadViewMatrix(drawingViewMatrix.create())
    textureShader.loadTransformationMatrix(transformationMatrix.create())

    enableAlphaBlending()
    drawTexture(it.model)

    disableVertexArrays()
    textureShader.stop()
}

var colorShader = ColorShader()
var colorArray: FloatArray = floatArrayOf(1f,1f,1f,1f)
var colorRenderer:(Drawer.Companion)-> Unit = {

    prepareVertexArrays(it.model.vaoID, 1)

    colorShader.start()
    colorShader.loadViewMatrix(it.viewMatrix.create())
    colorShader.loadTransformationMatrix(transformationMatrix.create())
    colorShader.setUniform4fv(colorShader.color, colorArray)

    if (it.model.drawType == GL_TRIANGLES) {
        glDrawElements(GL_TRIANGLES, it.model.vertexCount,
                GL_UNSIGNED_SHORT, 0)
    } else
        glDrawArrays(it.model.drawType, 0,
                it.model.vertexCount)

    colorShader.stop()

    disableVertexArrays()
}

val circleShader = CircleShader()
val circleModel = loadTexturedQuad(loadTexture(Res.image["oval"]), 0f, 1f, 1f, 0f)
private var border = 0.05

var circleRenderer:(Drawer.Companion)-> Unit = {
    prepareVertexArrays(circleModel.vaoID, 2)

    circleShader.start()
    circleShader.loadViewMatrix(it.viewMatrix.create())
    border = 0.03/((transformationMatrix.scaleX + transformationMatrix.scaleY))
    circleShader.loadTransformationMatrix(transformationMatrix.create())
    circleShader.setUniform4fv(circleShader.color, colorArray)
    circleShader.loadFloat(circleShader.strokeWidth, it.strokeWidth.toFloat())
    circleShader.loadFloat(circleShader.border, border.toFloat())

    enableAlphaBlending()
    drawTexture(circleModel)

    circleShader.stop()

    disableVertexArrays()
}

var fontShader = FontShader()
var fontRenderer:(Drawer.Companion)-> Unit = {
    val model = it.guiText.model
    prepareVertexArrays(model.vaoID, 2)
    enableAlphaBlending()


    fontShader.start()
    fontShader.loadTranslation(it.guiText.positionX, it.guiText.positionY)
    fontShader.setUniform4fv(fontShader.color, colorArray)
    fontShader.loadProjectionMatrix(Game.projectionMatrix)
    fontShader.loadViewMatrix(Game.uiViewMatrix)
    fontShader.loadTransformationMatrix(transformationMatrix)

    glActiveTexture(GL_TEXTURE0)
    glBindTexture(GL_TEXTURE_2D, model.texture.id)
//    glDrawElements(GL11.GL_TRIANGLES, circleModel.vertexCount,
//            GL11.GL_UNSIGNED_SHORT, 0)
    glDrawArrays(GL_TRIANGLES, 0, model.vertexCount)

    disableVertexArrays()
    fontShader.stop()
}

val inverseRes = floatArrayOf(1f/Game.width.toFloat(), 1f/Game.height.toFloat())
val fbo = Fbo(Game.width, Game.height)
val AAShader = FXAAShader()
var frameRenderer: (Model) -> Unit = {
    prepareVertexArrays(it.vaoID, 1)

    AAShader.start()
    AAShader.setUniform2fv(AAShader.inverseRes, inverseRes)

    GL13.glActiveTexture(GL13.GL_TEXTURE0)
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, it.texture.id)

    GL11.glDrawElements(GL11.GL_TRIANGLES, it.vertexCount,
            GL11.GL_UNSIGNED_SHORT, 0)

    AAShader.stop()

    disableVertexArrays()
}

val quad: Model = loadTexturedQuad(Image(0), -1f, 1f, 1f, -1f)
var backRenderer = {
    prepareVertexArrays(quad.vaoID, 1)

    glDrawElements(GL_TRIANGLES, quad.vertexCount,
            GL_UNSIGNED_SHORT, 0)

    disableVertexArrays()
}