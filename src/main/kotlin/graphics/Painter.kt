package graphics

import display.Game
import font.FontType
import font.GUIText
import loader.loadFont
import loader.loadTexturedQuad
import models.Model
import org.jbox2d.common.Vec2
import org.joml.Matrix4f
import graphics.drawer.Drawer
import util.units.Angle
import kotlin.math.atan2
import kotlin.math.hypot

/**
 * Created by domin on 28/10/2017.
 */
@Suppress("unused") // There will be many functions here that go unused most of the time
@Deprecated("Painter is being deprecated and won't work as normal, use Drawer instead")
class Painter {
    var colorRenderer: (Model) -> Unit = {}
    var textureRenderer: (Model) -> Unit = {}
    var circleRenderer: () -> Unit = {}
    var color: Long = 0xFFFFFFFF
        set(value) {
            var color = value
//        this.boxColor = boxColor;
            val a = color and 0xFF
            color = color - a shr 8
            val b = color and 0xFF
            color = color - b shr 8
            val g = color and 0xFF
            color = color - g shr 8
            val r = color and 0xFF
            colorArray[0] = r / 255f
            colorArray[1] = g / 255f
            colorArray[2] = b / 255f
            colorArray[3] = a / 255f

            field = value
        }

    val font: FontType = loadFont("arial")
    val guiText = GUIText("", 10f, font, 0f, 0f, 5f)
    val model = loadTexturedQuad(Image(0), 0f, 1f, 1f, 0f)


    fun drawRect(x: Number, y: Number, width: Number, height: Number) {
        transformationMatrix.setTranslate(x.toDouble(), y.toDouble(), 0.0)
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        colorRenderer(model)
        transformationMatrix.rewind()
    }

    fun drawCenteredRect(x: Number, y: Number, width: Number, height: Number) {
        drawRect(x.toDouble() - width.toDouble()/2.0, y.toDouble() - height.toDouble()/2.0, width.toDouble(), height.toDouble())
    }

    fun drawPositionalRect(left: Number, top: Number, right: Number, bottom: Number){
        drawRect(left, bottom, right.toDouble() - left.toDouble(), top.toDouble() - bottom.toDouble())
    }

    fun drawRotatedRect(centerX: Number, centerY: Number, width: Number, height: Number, angle: Angle) {
        transformationMatrix.setPivot(width.toDouble() / 2.0, height.toDouble() / 2.0)
        transformationMatrix.setTranslate(centerX.toDouble() - width.toDouble() / 2.0, centerY.toDouble() - height.toDouble() / 2.0, 0.0)
        transformationMatrix.setRotate(0.0, 0.0, angle.toDegrees().asDouble())
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        colorRenderer(model)
        transformationMatrix.setRotate(0.0, 0.0, 0.0)
    }

    fun drawScreenRect(x: Number, y: Number, width: Number, height: Number){
        transformationMatrix.setTranslate(x.toDouble(), y.toDouble(), 0.0)
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        drawingViewMatrix = Game.uiViewMatrix
        colorRenderer(model)
        drawingViewMatrix = Game.viewMatrix
    }

    fun fillPolygon(model: Model) {
        transformationMatrix.rewind()
        colorRenderer(model)
    }

    fun fillRotatedPolygon(model: Model, centerX: Number, centerY: Number, degrees: Number, pivotX: Number = 0.0, pivotY: Number = 0.0) {
        transformationMatrix.setPivot(pivotX.toDouble(), pivotY.toDouble())
        transformationMatrix.setTranslate(centerX.toDouble(), centerY.toDouble(), 0.0)
        transformationMatrix.setRotate(0.0, 0.0, degrees.toDouble())
        colorRenderer(model)
        transformationMatrix.setRotate(0.0, 0.0, 0.0)
        transformationMatrix.rewind()
    }

    fun fillPolygon(model: Model, x: Number, y: Number) {
        transformationMatrix.setTranslate(x.toDouble(), y.toDouble(), 0.0)
        colorRenderer(model)
    }

    fun fillPolygon(model: Model, x: Number, y: Number, scaleWidth: Number, scaleHeight: Number) {
        transformationMatrix.setTranslate(x.toDouble(), y.toDouble(), 0.0)
        transformationMatrix.setScale(scaleWidth.toDouble(), scaleHeight.toDouble(), 1.0)
        colorRenderer(model)
    }

    fun fillScreenPolygon(model: Model, x: Number, y: Number, scaleWidth: Number, scaleHeight: Number) {
        transformationMatrix.setTranslate(x.toDouble(), y.toDouble(), 0.0)
        transformationMatrix.setScale(scaleWidth.toDouble(), scaleHeight.toDouble(), 1.0)
        drawingViewMatrix = Game.uiViewMatrix
        colorRenderer(model)
        drawingViewMatrix = Game.viewMatrix
    }

    @Suppress("UNUSED_PARAMETER")
    fun fillScreenPolygon(model: Model, x: Number, y: Number, scaleWidth: Number, scaleHeight: Number, degrees: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun drawPolygon(vertices: Array<out Vec2>, strokeWidth: Number, vertexCount: Int = vertices.size, looped: Boolean = false) {
        for (i in 0..vertexCount-2) {
            val v1 = vertices[i]
            val v2 = vertices[i+1]
            drawLine(strokeWidth, v1.x, v1.y, v2.x, v2.y)
        }
        if (looped) {
            drawLine(strokeWidth, vertices[vertexCount-1].x, vertices[vertexCount-1].y, vertices[0].x, vertices[0].y)
        }
    }

    fun drawImage(image: Image) {
        model.texture = image
        textureRenderer(model)
    }

    fun drawScreenImage(image: Image, x: Number, y: Number, width: Number, height: Number) {
        transformationMatrix.setTranslate(x.toDouble(), y.toDouble(), 0.0)
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        model.texture = image
        drawingViewMatrix = Game.uiViewMatrix
        textureRenderer(model)
        drawingViewMatrix = Game.viewMatrix
    }

    fun drawCenteredScreenImage(image: Image, x: Number, y: Number, width: Number, height: Number) {
        transformationMatrix.setTranslate(x.toDouble() - width.toDouble()/2.0, y.toDouble() - height.toDouble()/2.0, 0.0)
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        model.texture = image
        drawingViewMatrix = Game.uiViewMatrix
        textureRenderer(model)
        drawingViewMatrix = Game.viewMatrix
    }

    fun drawImage(image: Image, matrix: Matrix4f) {
        transformationMatrix.setMatrix(matrix)
        model.texture = image
        textureRenderer(model)
    }

    fun drawImage(image: Image, x: Number, y: Number, width: Number, height: Number) {
        transformationMatrix.setTranslate(x.toDouble(), y.toDouble(), 0.0)
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        model.texture = image
        textureRenderer(model)
        transformationMatrix.rewind()
    }

    fun drawCenteredImage(image: Image, x: Number, y: Number, width: Number, height: Number) {
        drawImage(image, x.toDouble() - width.toDouble()/2.0, y.toDouble() - height.toDouble()/2.0, width.toDouble(), height.toDouble())
    }

    fun drawPositionalImage(image: Image, left: Number, top: Number, right: Number, bottom: Number) {
        drawImage(image, left, bottom, right.toDouble() - left.toDouble(), top.toDouble() - bottom.toDouble())
    }

    fun drawRotatedImage(image: Image, centerX: Number, centerY: Number, width: Number, height: Number, angle: Angle) {
        transformationMatrix.setPivot(width.toDouble() / 2.0, height.toDouble() / 2.0)
        transformationMatrix.setTranslate(centerX.toDouble() - width.toDouble() / 2.0, centerY.toDouble() - height.toDouble() / 2.0, 0.0)
        transformationMatrix.setRotate(0.0, 0.0, angle.toDegrees().asDouble())
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        model.texture = image
        textureRenderer(model)
        transformationMatrix.setRotate(0.0, 0.0, 0.0)
    }

    fun drawText(text: String, fontSize: Number, x: Number, y: Number) {
        guiText.set(text, fontSize.toFloat(), font, x.toFloat(), y.toFloat(), guiText.maxLineSize, guiText.isCentered)

        fontRenderer(Drawer)
        transformationMatrix.rewind()
    }


    fun drawCircle(centerX: Number, centerY: Number, radius: Number, strokeWidth: Number){
        transformationMatrix.setScale(radius.toDouble()*2.0, radius.toDouble()*2.0, 1.0)
        transformationMatrix.setTranslate(centerX.toDouble() - radius.toDouble(), centerY.toDouble() - radius.toDouble(), 0.0)
        circleRenderer(Drawer.Companion)
        transformationMatrix.rewind()
    }

    fun fillCircle(centerX: Number, centerY: Number, radius: Number){
        transformationMatrix.setScale(radius.toDouble()*2.0, radius.toDouble()*2.0, 1.0)
        transformationMatrix.setTranslate(centerX.toDouble() - radius.toDouble(), centerY.toDouble() - radius.toDouble(), 0.0)
        circleRenderer(Drawer.Companion)
        transformationMatrix.rewind()
    }

    fun drawEllipse(centerX: Number, centerY: Number, horizontalAxis: Number, verticalAxis: Number, strokeWidth: Number){
        transformationMatrix.setScale(horizontalAxis.toDouble(), verticalAxis.toDouble(), 1.0)
        transformationMatrix.setTranslate(centerX.toDouble() - horizontalAxis.toDouble()/2.0, centerY.toDouble() - verticalAxis.toDouble()/2.0, 0.0)
        circleRenderer(Drawer.Companion)
    }

    fun fillEllipse(centerX: Number, centerY: Number, horizontalAxis: Number, verticalAxis: Number){
        transformationMatrix.setScale(horizontalAxis.toDouble(), verticalAxis.toDouble(), 1.0)
        transformationMatrix.setTranslate(centerX.toDouble() - horizontalAxis.toDouble()/2.0, centerY.toDouble() - verticalAxis.toDouble()/2.0, 0.0)
        circleRenderer(Drawer.Companion)
    }

    fun fillRotatedEllipse(centerX: Number, centerY: Number, horizontalAxis: Number, verticalAxis: Number, degrees: Number){
        transformationMatrix.setPivot(horizontalAxis.toDouble() / 2.0, verticalAxis.toDouble() / 2.0)
        transformationMatrix.setScale(horizontalAxis.toDouble(), verticalAxis.toDouble(), 1.0)
        transformationMatrix.setTranslate(centerX.toDouble() - horizontalAxis.toDouble()/2.0, centerY.toDouble() - verticalAxis.toDouble()/2.0, 0.0)
        transformationMatrix.setRotate(0.0, 0.0, degrees.toDouble())
        circleRenderer(Drawer.Companion)
        transformationMatrix.setRotate(0.0, 0.0, 0.0)

    }

    fun drawLine(strokeWidth: Number, x1: Number, y1: Number, x2: Number, y2: Number) {
        val triangleWidth = x2.toDouble() - x1.toDouble()
        val triangleHeight = y2.toDouble() - y1.toDouble()
        transformationMatrix.setScale(hypot(triangleWidth, triangleHeight), strokeWidth.toDouble(), 1.0)

        transformationMatrix.setTranslate(x1.toDouble(), y1.toDouble() - strokeWidth.toDouble()/2.0, 0.0)
        transformationMatrix.setPivot(0.0, strokeWidth.toDouble() / 2.0)
        transformationMatrix.setRotate(0.0, 0.0, Math.toDegrees(atan2(triangleHeight, triangleWidth)))

        colorRenderer(model)
    }

    fun drawHorizontalLine(strokeWidth: Number, y: Number) {
        transformationMatrix.setScale(Game.viewWidth, strokeWidth.toDouble(), 0.0)
        transformationMatrix.setTranslate(Game.viewX - Game.viewWidth / 2.0, y.toDouble(), 0.0)

        colorRenderer(model)
    }

    fun drawVerticalLine(strokeWidth: Number, x: Number) {
        transformationMatrix.setScale(strokeWidth.toDouble(), Game.viewHeight, 0.0)
        transformationMatrix.setTranslate(x.toDouble(), Game.viewY - Game.viewHeight / 2.0, 0.0)

        colorRenderer(model)
    }
}