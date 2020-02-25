package drawer

import display.Game
import models.Model
import gl.renderer.*
import gl.renderer.ColorRenderer
import gl.utils.loadTextureUnitSquare
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
import util.extensions.radians
import util.units.Angle
import util.units.Vector
import kotlin.math.atan2
import kotlin.math.hypot


internal class DrawerImpl : ColorDrawer, RotatedDrawer, StrokeDrawer {

    private var layout: Layouts = Layouts.NORMAL
    private var frame: Frames = Frames.WORLD

    private var layoutWasSet: Boolean = false
    private var frameWasSet: Boolean = false
    private var strokeWasSet: Boolean = false

    private var wasRotated: Boolean = false
    private var wasPivoted: Boolean = false
    private var angle: Angle = 0.degrees

    private var pivotX: Double = 0.0
    private var pivotY: Double = 0.0

    private var strokeWidth: Double = 0.3

    private val transformation = TransformationMatrix()

    private val vbo = VBO.create(loadUnitSquare(), positionAttribute)
    private val texVbo = VBO.create(loadTextureUnitSquare(), texCoordsAttribute)
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

        renderer.view = when (frame) {
            Frames.UI -> {
                Game.uiViewMatrix.get()
            }
            Frames.WORLD -> {
                Game.viewMatrix.get()
            }
        }

        if (wasRotated) {
            if (!wasPivoted) {
                pivotX = width/2.0
                pivotY = height/2.0
            }
            rotate(angle.toDegrees().asDouble(), pivotX, pivotY)
        }
        transformation.setTranslate(outX, outY, 0.0)
        transformation.setScale(width, height, 1.0)

        renderer.render(drawable, transformation.get())
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
        strokeWasSet = false

        pivotX = 0.0
        pivotY = 0.0

        strokeWidth = 0.3

        imageRenderer.alpha = 1f

        transformation.rewind()

    }

    private fun rotate(degrees: Double, pivotX: Double, pivotY: Double) {
        transformation.setPivot(pivotX, pivotY)
        transformation.setRotate(0.0, 0.0, degrees)
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
        if (!frameWasSet) {
            frame = Frames.UI
        }
        fontRenderer.set(text, fontSize.toFloat(), x = x.toFloat(), y = y.toFloat())
        renderer = fontRenderer
        draw(0.0, 0.0, 1.0, 1.0)
    }

    override fun image(image: Image, x: Number, y: Number, width: Number, height: Number) {
        if (!layoutWasSet) {
            layout = Layouts.CENTERED
        }
        drawable.image = image
        renderer = imageRenderer
        draw(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

    override fun polygon(polygon: Polygon, x: Number, y: Number, scaleWidth: Number, scaleHeight: Number) {
        polygonRenderer.polygon = polygon
        if (strokeWasSet) {
            renderer = colorRenderer
            val p = polygon.path
            path(p, x, y, scaleWidth, scaleHeight)
            drawLineForPath(p[p.size-1], p[0], x.toDouble(), y.toDouble(), scaleWidth.toDouble(), scaleHeight.toDouble())
        } else {
            renderer = polygonRenderer
            layout = Layouts.NORMAL
            draw(x.toDouble(), y.toDouble(), scaleWidth.toDouble(), scaleHeight.toDouble())
        }
    }

    override fun path(path: List<Vector>, x: Number, y: Number, scaleWidth: Number, scaleHeight: Number) {
        for (i in 0..path.size-2) {
            drawLineForPath(path[i], path[i+1], x.toDouble(), y.toDouble(), scaleWidth.toDouble(), scaleHeight.toDouble())
        }
    }

    override fun line(x1: Number, y1: Number, x2: Number, y2: Number) {
        val triangleWidth = x2.toDouble() - x1.toDouble()
        val triangleHeight = y2.toDouble() - y1.toDouble()

        val angle = Math.toDegrees(atan2(triangleHeight, triangleWidth))
        rotate(angle, 0.0, strokeWidth/2.0)
        wasRotated = false
        renderer = colorRenderer
        draw(x1.toDouble(), y1.toDouble(), hypot(triangleWidth, triangleHeight), strokeWidth)
    }

    private fun drawLineForPath(p1: Vector, p2: Vector, x: Double = 0.0, y: Double = 0.0, scaleWidth: Double = 1.0, scaleHeight: Double = 1.0) {
        val (x1, y1) = p1
        val (x2, y2) = p2
        val stroke = strokeWidth
        line(
                x1 * scaleWidth + x,
                y1 * scaleHeight + y,
                x2 * scaleWidth + x,
                y2 * scaleHeight + y)
        strokeWidth = stroke
    }

    override val normal: Drawer
        get() {
            layoutWasSet = true
            layout = Layouts.NORMAL
            return this
        }
    override val centered: Drawer
        get() {
            layoutWasSet = true
            layout = Layouts.CENTERED
            return this
        }

    override val color: ColorDrawer
        get() {
            return this
        }

    override val stroke: StrokeDrawer
        get() {
            strokeWasSet = true
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
        imageRenderer.alpha = color.a.toFloat()
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
        strokeWasSet = true
        strokeWidth = stroke
        circleRenderer.strokeWidth = stroke.toFloat()
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