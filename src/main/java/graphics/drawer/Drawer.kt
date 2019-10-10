package graphics.drawer

import display.Game
import font.FontType
import font.GUIText
import loader.loadTexturedQuad
import graphics.*
import loader.loadFont
import matrices.ViewMatrix
import util.colors.hex
import util.extensions.degrees
import util.units.Angle
import kotlin.math.atan2

open class Drawer {
    companion object {
        private val quad = loadTexturedQuad(Image(0), 0f, 1f, 1f, 0f)
        var model = quad

        var renderer: (Companion)-> Unit = colorRenderer

        var isLayoutSet = false
        var layout: Layout = Normal

        var color = ColorDrawer

        var isRotationSet = false
        var angle: Angle = 0.degrees
        var isPivotSet = false
        var pivotX: Double = 0.0
        var pivotY: Double = 0.0

        var isStrokeSet = false
        var strokeWidth: Double = 1.0

        var isMatrixSet = false
        var viewMatrix: ViewMatrix = Game.viewMatrix

        val font: FontType = loadFont("arial")
        val guiText = GUIText("", 10f, font, 0f, 0f, 5f)

        private fun reset() {
            layout = Normal
            isRotationSet = false
            isPivotSet = false
            isLayoutSet = false
            isMatrixSet = false
            isStrokeSet = false
            strokeWidth = 1.0
            model = quad
            viewMatrix = Game.viewMatrix
            transformationMatrix.setRotate(0.0, 0.0, 0.0)
            transformationMatrix.rewind()
        }

        private fun draw() {
            val l = layout
            if (!isRotationSet) {
                transformationMatrix.setTranslate(l.outX, l.outY, 0.0)
            } else {
                if (!isPivotSet) {
                    transformationMatrix.setPivot(l.outWidth / 2.0, l.outHeight / 2.0)
                } else {
                    transformationMatrix.setPivot(pivotX, pivotY)
                }
                transformationMatrix.setTranslate(l.outX, l.outY, 0.0)
                transformationMatrix.setRotate(0.0, 0.0, angle.toDegrees().toDouble())
            }
            transformationMatrix.setScale(l.outWidth, l.outHeight, 1.0)
            renderer(this)
            reset()
        }

        fun drawLine(strokeWidth: Double, x1: Double, y1: Double, x2: Double, y2: Double) {
            val triangleWidth = x2 - x1
            val triangleHeight = y2 - y1
            transformationMatrix.setScale(Math.hypot(triangleWidth, triangleHeight), strokeWidth, 1.0)

            transformationMatrix.setTranslate(x1, y1 - strokeWidth/2.0, 0.0)
            transformationMatrix.setPivot(0.0, strokeWidth / 2.0)
            transformationMatrix.setRotate(0.0, 0.0, Math.toDegrees(atan2(triangleHeight, triangleWidth)))

            colorRenderer(this)
            reset()
        }
    }


    fun rectangle(x: Number, y: Number, width: Number, height: Number){
        renderer = colorRenderer
        layout(x,y,width, height)
        draw()
    }

    fun circle(x: Number, y: Number, radius: Number) {
        val diameter = 2.0*radius.toDouble()
        if (!isStrokeSet) {
            strokeWidth = 1.0
        }
        ellipse(x, y, diameter, diameter)
    }

    fun ellipse(x: Number, y: Number, width: Number, height: Number) {
        if (!isLayoutSet) {
            layout = Centered
        }
        renderer = circleRenderer
        layout(x, y, width, height)
        draw()
    }

    fun text(text: String, fontSize: Number, x: Number, y: Number) {
        if (!isMatrixSet) {
            viewMatrix = Game.uiViewMatrix
        }
        guiText.set(text, fontSize.toFloat(), font, x.toFloat(), y.toFloat(), guiText.maxLineSize, guiText.isCentered)
        layout(0.0, 0.0, 1.0, 1.0)
        renderer = fontRenderer
        draw()
    }

    fun image(image: Image, x: Number, y: Number, width: Number, height: Number) {
        model.texture = image
        renderer = textureRenderer
        layout(x,y,width,height)
        draw()
    }

    fun polygon(polygon: Polygon, x: Number, y: Number, scaleWidth: Number = 1.0, scaleHeight: Number = 1.0, looped: Boolean = true) {
        if (isStrokeSet) {
            val p = polygon.path
            for (i in 0..p.size-2) {
                val v1 = p[i]
                val v2 = p[i+1]
                drawLine(strokeWidth, v1.x, v1.y, v2.x, v2.y)
            }
            if (looped) {
                drawLine(strokeWidth, p[p.size-1].x, p[p.size-1].y, p[0].x, p[0].y)
            }
        } else {
            layout = Normal
            layout(x,y, scaleWidth, scaleHeight)
            renderer = colorRenderer
            model = polygon.model
            draw()
        }
    }

    fun line(x1: Number, y1: Number, x2: Number, y2: Number) {
        drawLine(strokeWidth, x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
    }


    val normal: Drawer
        get() {
            layout = Normal
            isLayoutSet = true
            return this
        }
    val centered: Drawer
        get() {
            layout = Centered
            isLayoutSet = true
            return this
        }
    @Suppress("LeakingThis") // Leaking this is okay because no processing is being done on 'this' inside the contructor
    val positional: Positional = Positional(this)
        get() {
            isLayoutSet = true
            return field
        }

    val color: ColorDrawer
        get() = ColorDrawer

    val red: Drawer
        get() {
            ColorDrawer(hex(0xFF0000FF))
            return this
        }
    val green: Drawer
        get() {
            ColorDrawer(hex(0x00FF00FF))
            return this
        }
    val blue: Drawer
        get() {
            ColorDrawer(hex(0x0000FFFF))
            return this
        }
    val magenta: Drawer
        get() {
            ColorDrawer(hex(0xFF00FFFF))
            return this
        }
    val cyan: Drawer
        get() {
            ColorDrawer(hex(0x00FFFFFF))
            return this
        }
    val yellow: Drawer
        get() {
            ColorDrawer(hex(0xFFFF00FF))
            return this
        }

    val stroke: Stroke
        get() {
            isStrokeSet = true
            return Stroke
        }

    val rotated: Rotated
        get() {
            isRotationSet = true
            return Rotated
        }

    val ui: Drawer
        get() {
            isMatrixSet = true
            viewMatrix = Game.uiViewMatrix
            return this
        }
    val world: Drawer
        get() {
            isMatrixSet = true
            viewMatrix = Game.viewMatrix
            return this
        }

    private operator fun Layout.invoke(x: Number, y: Number, width: Number, height: Number) {
        this.x = x.toDouble()
        this.y = y.toDouble()
        this.width = width.toDouble()
        this.height = height.toDouble()
    }
}