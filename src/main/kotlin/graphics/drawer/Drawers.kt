package graphics.drawer

import gl.*
import gl.renderer.CircleRenderer
import gl.renderer.ImageRenderer
import gl.renderer.Renderer
import graphics.*
import matrices.TransformationMatrix
import util.colors.Color
import util.colors.hex
import util.colors.rgba2Hex
import util.colors.toColor
import util.extensions.degrees
import util.extensions.vec
import util.units.Angle
import util.units.Vector

interface BaseDrawer {
    fun rectangle(x: Number, y: Number, width: Number, height: Number)

    fun circle(x: Number, y: Number, radius: Number) {
        val diameter = 2.0*radius.toDouble()
        ellipse(x, y, diameter, diameter)
    }

    fun circle(position: Vector, radius: Number) = circle(position.x, position.y, radius)

    fun ellipse(x: Number, y: Number, width: Number, height: Number)

    fun text(text: String, fontSize: Number, x: Number, y: Number)

    fun image(image: Image, x: Number, y: Number, width: Number, height: Number)

    fun polygon(polygon: Polygon, x: Number = 0.0, y: Number = 0.0, scaleWidth: Number = 1.0, scaleHeight: Number = 1.0)

    fun path(path: List<Vector>, x: Number = 0.0, y: Number = 0.0, scaleWidth: Number = 1.0, scaleHeight: Number = 1.0)

    fun line(x1: Number, y1: Number, x2: Number, y2: Number)

    fun line(p1: Vector, p2: Vector) = line(p1.x, p1.y, p2.x, p2.y)

    val normal: BaseDrawer
    val centered: BaseDrawer

//    @Suppress("LeakingThis") // Leaking this is okay because no processing is being done on 'this' inside the constructor
//    val positional: Positional = Positional(this)
//        get() {
//            Drawer.isLayoutSet = true
//            return field
//        }

    val color: ColorDrawer2

    val black: BaseDrawer
        get() {
            color(hex(0x000000FF))
            return this
        }
    val white: BaseDrawer
        get() {
            color(hex(0xFFFFFFFF))
            return this
        }
    val red: BaseDrawer
        get() {
            color(hex(0xFF0000FF))
            return this
        }
    val green: BaseDrawer
        get() {
            color(hex(0x00FF00FF))
            return this
        }
    val blue: BaseDrawer
        get() {
            color(hex(0x0000FFFF))
            return this
        }
    val magenta: BaseDrawer
        get() {
            color(hex(0xFF00FFFF))
            return this
        }
    val cyan: BaseDrawer
        get() {
            color(hex(0x00FFFFFF))
            return this
        }
    val yellow: BaseDrawer
        get() {
            color(hex(0xFFFF00FF))
            return this
        }

    val stroke: StrokeDrawer

    val rotated: RotatedDrawer

    val ui: BaseDrawer
    val world: BaseDrawer

}

interface ColorDrawer2 : BaseDrawer, Color {
    fun get(): Color

    operator fun invoke(color: Color): BaseDrawer

    operator fun invoke(hex: Long): BaseDrawer = invoke(hex(hex))

}

interface RotatedDrawer : BaseDrawer {
    operator fun invoke(angle: Angle): RotatedDrawer

    fun about(pivotX: Number, pivotY: Number): BaseDrawer
}

interface StrokeDrawer : BaseDrawer {
    operator fun invoke(stroke: Double): BaseDrawer
}

internal class DrawerImpl : ColorDrawer2, RotatedDrawer, StrokeDrawer {

    private var layout: Layouts = Layouts.NORMAL
    private var frame: Frames = Frames.WORLD

    private var wasRotated: Boolean = false
    private var wasPivoted: Boolean = false
    private var angle: Angle = 0.degrees
    private var layoutWasSet: Boolean = false
    private var frameWasSet: Boolean = false

    private var hasStroke: Boolean = false

    private var pivotX: Double = 0.0
    private var pivotY: Double = 0.0

    private var strokeWidth: Double = 0.3

    private val transformation = TransformationMatrix()

    private val vbo = VBO.create(loadUnitSquare(), positionAttribute)
    private val texVbo = VBO.create(loadUnitSquare(), texCoordsAttribute)
    private val drawable = Drawable(vbo, texVbo)

    private val colorRenderer = ColorRenderer()
    private val imageRenderer = ImageRenderer()
    private val circleRenderer = CircleRenderer()

    private var renderer: Renderer = colorRenderer

    var colorArray: FloatArray = floatArrayOf(1f,1f,1f,1f)

    private fun draw(x: Double, y: Double, width: Double, height: Double) {
        var outX = x
        var outY = y

        if (layout == Layouts.CENTERED) {
            outX = x - width /2.0
            outY = y - height/2.0
        }

        if (!wasRotated) {
            transformation.setTranslate(outX, outY, 0.0)
        } else {
            if (!wasPivoted) {
                transformation.setPivot(width / 2.0, height / 2.0)
            } else {
                transformation.setPivot(pivotX, pivotY)
            }
            transformation.setTranslate(outX, outY, 0.0)
            transformation.setRotate(0.0, 0.0, angle.toDegrees().asDouble())
        }
        transformation.setScale(width, height, 1.0)
        transformation.setScale(width, height, 1.0)

        renderer.render(drawable, transformation.create())
        reset()
    }

    private fun reset() {
        layout = Layouts.NORMAL
        frame = Frames.WORLD

        wasRotated = false
        wasPivoted = false
        angle = 0.degrees

        layoutWasSet = false
        frameWasSet = false

        hasStroke = false

        pivotX = 0.0
        pivotY = 0.0

        strokeWidth = 0.3

        transformation.rewind()

    }

    override fun rectangle(x: Number, y: Number, width: Number, height: Number) {
        draw(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

    override fun ellipse(x: Number, y: Number, width: Number, height: Number) {
        renderer = circleRenderer
        if (!layoutWasSet) {
            layout = Layouts.CENTERED
        }
        draw(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

    override fun text(text: String, fontSize: Number, x: Number, y: Number) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun image(image: Image, x: Number, y: Number, width: Number, height: Number) {
        drawable.image = image
        renderer = imageRenderer
        draw(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

    override fun polygon(polygon: Polygon, x: Number, y: Number, scaleWidth: Number, scaleHeight: Number) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun path(path: List<Vector>, x: Number, y: Number, scaleWidth: Number, scaleHeight: Number) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun line(x1: Number, y1: Number, x2: Number, y2: Number) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val normal: BaseDrawer
        get() {
            layout = Layouts.NORMAL
            return this
        }
    override val centered: BaseDrawer
        get() {
            layout = Layouts.CENTERED
            return this
        }

    override val color: ColorDrawer2
        get() {
            return this
        }

    override val stroke: StrokeDrawer
        get() {
            hasStroke = true
            return this
        }

    override val rotated: RotatedDrawer
        get() {
            wasRotated = true
            return this
        }

    override val ui: BaseDrawer
        get() {
            frame = Frames.UI
            return this
        }
    override val world: BaseDrawer
        get() {
            frame = Frames.WORLD
            return this
        }

    override fun get() = graphics.colorArray.toColor()

    override fun invoke(color: Color): BaseDrawer {
        colorArray[0] = color.r.toFloat()
        colorArray[1] = color.g.toFloat()
        colorArray[2] = color.b.toFloat()
        colorArray[3] = color.a.toFloat()

        colorRenderer.color = color
        circleRenderer.color = color
        return this
    }

    override fun invoke(angle: Angle): RotatedDrawer {
        wasRotated = true
        this.angle = angle
        return this
    }

    override fun about(pivotX: Number, pivotY: Number): BaseDrawer {
        wasPivoted = true
        this.pivotX = pivotX.toDouble()
        this.pivotY = pivotY.toDouble()
        return this
    }

    override fun invoke(stroke: Double): BaseDrawer {
        hasStroke = true
        strokeWidth = stroke
        return this
    }

    private enum class Layouts {
        NORMAL,
        CENTERED
    }

    private enum class Frames {
        WORLD,
        UI
    }

    override val a: Double
        get() = colorArray[3].toDouble()
    override val r: Double
        get() = colorArray[0].toDouble()
    override val g: Double
        get() = colorArray[1].toDouble()
    override val b: Double
        get() = colorArray[2].toDouble()

    override fun toLong() = rgba2Hex(r,g,b,a)
}
