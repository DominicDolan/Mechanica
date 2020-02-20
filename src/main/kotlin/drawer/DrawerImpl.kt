package drawer

import models.Model
import gl.renderer.*
import gl.renderer.ColorRenderer
import gl.utils.loadUnitSquare
import gl.utils.positionAttribute
import gl.utils.texCoordsAttribute
import gl.vbo.VBO
import graphics.Image
import graphics.Polygon
import matrices.TransformationMatrix
import util.colors.Color
import util.colors.rgba2Hex
import util.colors.toColor
import util.extensions.component1
import util.extensions.component2
import util.extensions.degrees
import util.units.Angle
import util.units.Vector
import kotlin.math.atan2
import kotlin.math.hypot


internal class DrawerImpl : ColorDrawer, RotatedDrawer, StrokeDrawer {

    private var layout: Layouts = Layouts.NORMAL
    private var frame: Frames = Frames.WORLD

    private var layoutWasSet: Boolean = false
    private var frameWasSet: Boolean = false

    private var wasRotated: Boolean = false
    private var wasPivoted: Boolean = false
    private var angle: Angle = 0.degrees

    private var hasStroke: Boolean = false

    private var pivotX: Double = 0.0
    private var pivotY: Double = 0.0

    private var strokeWidth: Double = 0.3

    private val transformation = TransformationMatrix()

    private val vbo = VBO.create(loadUnitSquare(), positionAttribute)
    private val texVbo = VBO.create(loadUnitSquare(), texCoordsAttribute)
    private val drawable = Model(vbo, texVbo)

    private val colorRenderer = ColorRenderer()
    private val imageRenderer = ImageRenderer()
    private val circleRenderer = CircleRenderer()
    private val fontRenderer = FontRenderer()
    private val polygonRenderer = PolygonRenderer()

    private var renderer: Renderer = colorRenderer

    private var colorArray: FloatArray = floatArrayOf(1f,1f,1f,1f)

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
        renderer = colorRenderer
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
//        if (!frameWasSet) {
//            frame = Frames.UI
//        }
//        fontRenderer.guiText.set(text, fontSize.toFloat(), fontRenderer.font, x.toFloat(), y.toFloat(), fontRenderer.guiText.maxLineSize, fontRenderer.guiText.isCentered)
        fontRenderer.set(text, fontSize.toFloat(), x = x.toFloat(), y = y.toFloat())
        renderer = fontRenderer
        draw(0.0, 0.0, 1.0, 1.0)
    }

    override fun image(image: Image, x: Number, y: Number, width: Number, height: Number) {
        drawable.image = image
        renderer = imageRenderer
        draw(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

    override fun polygon(polygon: Polygon, x: Number, y: Number, scaleWidth: Number, scaleHeight: Number) {
        polygonRenderer.polygon = polygon
        renderer = polygonRenderer
        draw(0.0, 0.0, 1.0, 1.0)
    }

    override fun path(path: List<Vector>, x: Number, y: Number, scaleWidth: Number, scaleHeight: Number) {
        for (i in 0..path.size-2) {
            drawLineForPath(path[i], path[i+1], x, y, scaleWidth, scaleHeight)
        }
    }

    override fun line(x1: Number, y1: Number, x2: Number, y2: Number) {
        val triangleWidth = x2.toDouble() - x1.toDouble()
        val triangleHeight = y2.toDouble() - y1.toDouble()
        transformation.setScale(hypot(triangleWidth, triangleHeight), strokeWidth, 1.0)

        transformation.setTranslate(x1.toDouble(), y1.toDouble() - strokeWidth/2.0, 0.0)
        transformation.setPivot(0.0, strokeWidth / 2.0)
        transformation.setRotate(0.0, 0.0, Math.toDegrees(atan2(triangleHeight, triangleWidth)))

        colorRenderer.render(drawable, transformation.create())
        reset()
    }

    private fun drawLineForPath(p1: Vector, p2: Vector, x: Number = 0.0, y: Number = 0.0, scaleWidth: Number = 1.0, scaleHeight: Number = 1.0) {
        val (x1, y1) = p1
        val (x2, y2) = p2
        line(
                x1 * scaleWidth.toDouble() + x.toDouble(),
                y1 * scaleHeight.toDouble() + y.toDouble(),
                x2 * scaleWidth.toDouble() + x.toDouble(),
                y2 * scaleHeight.toDouble() + y.toDouble())
    }

    override val normal: Drawer
        get() {
            layout = Layouts.NORMAL
            return this
        }
    override val centered: Drawer
        get() {
            layout = Layouts.CENTERED
            return this
        }

    override val color: ColorDrawer
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

    override val ui: Drawer
        get() {
            frame = Frames.UI
            return this
        }
    override val world: Drawer
        get() {
            frame = Frames.WORLD
            return this
        }

    override fun get() = colorArray.toColor()

    override fun invoke(color: Color): Drawer {
        colorArray[0] = color.r.toFloat()
        colorArray[1] = color.g.toFloat()
        colorArray[2] = color.b.toFloat()
        colorArray[3] = color.a.toFloat()

        colorRenderer.color = color
        circleRenderer.color = color
        fontRenderer.color = color
        polygonRenderer.color = color
        return this
    }

    override fun invoke(angle: Angle): RotatedDrawer {
        wasRotated = true
        this.angle = angle
        return this
    }

    override fun about(pivotX: Number, pivotY: Number): Drawer {
        wasPivoted = true
        this.pivotX = pivotX.toDouble()
        this.pivotY = pivotY.toDouble()
        return this
    }

    override fun invoke(stroke: Double): Drawer {
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