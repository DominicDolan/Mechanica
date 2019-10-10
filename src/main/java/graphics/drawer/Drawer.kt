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

open class Drawer {
    companion object {
        val model = loadTexturedQuad(0, 0f, 1f, 1f, 0f)

        var renderer: (Companion)-> Unit = colorRenderer

        var isLayoutSet = false
        var layout: Layout = Normal

        var color = ColorDrawer

        var isRotationSet = false
        var angle: Angle = 0.degrees
        var isPivotSet = false
        var pivotX: Double = 0.0
        var pivotY: Double = 0.0

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

    fun rectangle(x: Number, y: Number, width: Number, height: Number){
        renderer = colorRenderer
        layout(x,y,width, height)
        draw()
    }

    fun circle(x: Number, y: Number, radius: Number) {
        val diameter = 2.0*radius.toDouble()
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

    fun image(image: Int, x: Number, y: Number, width: Number, height: Number) {
        model.texture = image
        renderer = textureRenderer
        layout(x,y,width,height)
        draw()
    }

    private operator fun Layout.invoke(x: Number, y: Number, width: Number, height: Number) {
        this.x = x.toDouble()
        this.y = y.toDouble()
        this.width = width.toDouble()
        this.height = height.toDouble()
    }
}