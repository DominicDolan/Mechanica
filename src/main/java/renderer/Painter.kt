package renderer

import display.Game
import font.FontType
import font.GUIText
import loader.loadFont
import loader.loadTexturedQuad
import models.Model
import org.joml.Matrix4f

/**
 * Created by domin on 28/10/2017.
 */
class Painter {
    var colorRenderer: (Model) -> Unit = renderer.colorRenderer
    var textureRenderer: (Model) -> Unit = renderer.textureRenderer
    var circleRenderer: () -> Unit = renderer.circleRenderer
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
    val model = loadTexturedQuad(0, 0f, 1f, 1f, 0f)


    fun drawRect(x: Number, y: Number, width: Number, height: Number) {
        transformationMatrix.setTranslate(x.toDouble(), y.toDouble(), 0.0)
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        colorRenderer(model)
    }

    fun drawCenteredRect(x: Number, y: Number, width: Number, height: Number) {
        drawRect(x.toDouble() - width.toDouble()/2.0, y.toDouble() - height.toDouble()/2.0, width.toDouble(), height.toDouble())
    }

    fun drawPositionalRect(left: Number, top: Number, right: Number, bottom: Number){
        drawRect(left, bottom, right.toDouble() - left.toDouble(), top.toDouble() - bottom.toDouble())
    }

    fun drawRotatedRect(centerX: Number, centerY: Number, width: Number, height: Number, degrees: Number) {
        transformationMatrix.setPivot(width.toDouble() / 2.0, height.toDouble() / 2.0)
        transformationMatrix.setTranslate(centerX.toDouble() - width.toDouble() / 2.0, centerY.toDouble() - height.toDouble() / 2.0, 0.0)
        transformationMatrix.setRotate(0.0, 0.0, degrees.toDouble())
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        colorRenderer(model)
        transformationMatrix.setRotate(0.0, 0.0, 0.0)
    }

    fun drawScreenRect(x: Number, y: Number, width: Number, height: Number){
        transformationMatrix.setTranslate(x.toDouble(), y.toDouble(), 0.0)
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        renderer.drawingViewMatrix = Game.uiViewMatrix
        colorRenderer(model)
        renderer.drawingViewMatrix = Game.viewMatrix
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
        renderer.drawingViewMatrix = Game.uiViewMatrix
        colorRenderer(model)
        renderer.drawingViewMatrix = Game.viewMatrix
    }

    fun fillScreenPolygon(model: Model, x: Number, y: Number, scaleWidth: Number, scaleHeight: Number, degrees: Float) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun drawImage(image: Int) {
        model.texture = image
        textureRenderer(model)
    }

    fun drawScreenImage(image: Int, x: Number, y: Number, width: Number, height: Number) {
        transformationMatrix.setTranslate(x.toDouble(), y.toDouble(), 0.0)
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        model.texture = image
        renderer.drawingViewMatrix = Game.uiViewMatrix
        textureRenderer(model)
        renderer.drawingViewMatrix = Game.viewMatrix
    }

    fun drawCenteredScreenImage(image: Int, x: Number, y: Number, width: Number, height: Number) {
        transformationMatrix.setTranslate(x.toDouble() - width.toDouble()/2.0, y.toDouble() - height.toDouble()/2.0, 0.0)
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        model.texture = image
        renderer.drawingViewMatrix = Game.uiViewMatrix
        textureRenderer(model)
        renderer.drawingViewMatrix = Game.viewMatrix
    }

    fun drawImage(image: Int, matrix: Matrix4f) {
        transformationMatrix.setMatrix(matrix)
        model.texture = image
        textureRenderer(model)
    }

    fun drawImage(image: Int, x: Number, y: Number, width: Number, height: Number) {
        transformationMatrix.setTranslate(x.toDouble(), y.toDouble(), 0.0)
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        model.texture = image
        textureRenderer(model)
    }

    fun drawCenteredImage(image: Int, x: Number, y: Number, width: Number, height: Number) {
        drawImage(image, x.toDouble() - width.toDouble()/2.0, y.toDouble() - height.toDouble()/2.0, width.toDouble(), height.toDouble())
    }

    fun drawPositionalImage(image: Int, left: Number, top: Number, right: Number, bottom: Number) {
        drawImage(image, left, bottom, right.toDouble() - left.toDouble(), top.toDouble() - bottom.toDouble())
    }

    fun drawRotatedImage(image: Int, centerX: Number, centerY: Number, width: Number, height: Number, degrees: Number) {
        transformationMatrix.setPivot(width.toDouble() / 2.0, height.toDouble() / 2.0)
        transformationMatrix.setTranslate(centerX.toDouble() - width.toDouble() / 2.0, centerY.toDouble() - height.toDouble() / 2.0, 0.0)
        transformationMatrix.setRotate(0.0, 0.0, degrees.toDouble())
        transformationMatrix.setScale(width.toDouble(), height.toDouble(), 1.0)
        colorRenderer(model)
        transformationMatrix.setRotate(0.0, 0.0, 0.0)
    }

    fun drawText(text: String, fontSize: Number, x: Number, y: Number) {
        transformationMatrix.rewind()
        transformationMatrix.setScale(100.0, 100.0, 1.0)
        guiText.set(text, fontSize.toFloat(), font, x.toFloat(), y.toFloat(), guiText.maxLineSize, guiText.isCentered)

        renderer.fontRenderer(guiText)
    }


    fun drawCircle(centerX: Number, centerY: Number, radius: Number, strokeWidth: Number){
        transformationMatrix.setScale(radius.toDouble()*2.0, radius.toDouble()*2.0, 1.0)
        transformationMatrix.setTranslate(centerX.toDouble() - radius.toDouble(), centerY.toDouble() - radius.toDouble(), 0.0)
        renderer.strokeWidth = strokeWidth.toDouble()/radius.toDouble()
        renderer.circleRenderer()
    }

    fun fillCircle(centerX: Number, centerY: Number, radius: Number){
        transformationMatrix.setScale(radius.toDouble()*2.0, radius.toDouble()*2.0, 1.0)
        transformationMatrix.setTranslate(centerX.toDouble() - radius.toDouble(), centerY.toDouble() - radius.toDouble(), 0.0)
        renderer.strokeWidth = 1.0
        renderer.circleRenderer()
        transformationMatrix.rewind()
    }

    fun drawEllipse(centerX: Number, centerY: Number, horizontalAxis: Number, verticalAxis: Number, strokeWidth: Number){
        transformationMatrix.setScale(horizontalAxis.toDouble(), verticalAxis.toDouble(), 1.0)
        transformationMatrix.setTranslate(centerX.toDouble() - horizontalAxis.toDouble()/2.0, centerY.toDouble() - verticalAxis.toDouble()/2.0, 0.0)
        renderer.strokeWidth = 4.0*strokeWidth.toDouble()/(horizontalAxis.toDouble() + verticalAxis.toDouble())
        renderer.circleRenderer()
    }

    fun fillEllipse(centerX: Number, centerY: Number, horizontalAxis: Number, verticalAxis: Number){
        transformationMatrix.setScale(horizontalAxis.toDouble(), verticalAxis.toDouble(), 1.0)
        transformationMatrix.setTranslate(centerX.toDouble() - horizontalAxis.toDouble()/2.0, centerY.toDouble() - verticalAxis.toDouble()/2.0, 0.0)
        renderer.strokeWidth = 1.0
        renderer.circleRenderer()
    }

    fun fillRotatedEllipse(centerX: Number, centerY: Number, horizontalAxis: Number, verticalAxis: Number, degrees: Number){
        transformationMatrix.setPivot(horizontalAxis.toDouble() / 2.0, verticalAxis.toDouble() / 2.0)
        transformationMatrix.setScale(horizontalAxis.toDouble(), verticalAxis.toDouble(), 1.0)
        transformationMatrix.setTranslate(centerX.toDouble() - horizontalAxis.toDouble()/2.0, centerY.toDouble() - verticalAxis.toDouble()/2.0, 0.0)
        transformationMatrix.setRotate(0.0, 0.0, degrees.toDouble())
        renderer.strokeWidth = 1.0
        renderer.circleRenderer()
        transformationMatrix.setRotate(0.0, 0.0, 0.0)

    }

    fun drawLine(strokeWidth: Number, x1: Number, y1: Number, x2: Number, y2: Number) {
        val triangleWidth = x2.toDouble() - x1.toDouble()
        val triangleHeight = y2.toDouble() - y1.toDouble()
        transformationMatrix.setScale(Math.hypot(triangleWidth, triangleHeight), strokeWidth.toDouble(), 1.0)

        transformationMatrix.setTranslate(x1.toDouble(), y1.toDouble(), 0.0)
        transformationMatrix.setPivot(0.0, strokeWidth.toDouble() / 2.0)
        transformationMatrix.setRotate(0.0, 0.0, Math.toDegrees(Math.atan2(triangleHeight, triangleWidth)))

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