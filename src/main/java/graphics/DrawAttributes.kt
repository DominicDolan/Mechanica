package graphics

import graphics.drawer.Drawer
import util.colors.Color
import util.colors.hex
import util.units.Angle


object Centered : Layout() {
    override val outX: Double
        get() = x - width/2.0
    override val outY: Double
        get() = y - height/2.0
    override val outWidth: Double
        get() = width
    override val outHeight: Double
        get() = height
}

class Positional(private val drawer: Drawer) {
    fun rectangle(left: Number, top: Number, right: Number, bottom: Number) {
        Drawer.layout = Normal
        drawer.rectangle(left, bottom, right.toDouble() - left.toDouble(), top.toDouble() - bottom.toDouble())
    }

    fun ellipse(left: Number, top: Number, right: Number, bottom: Number) {
        Drawer.layout = Normal
        drawer.ellipse(left, bottom, right.toDouble() - left.toDouble(), top.toDouble() - bottom.toDouble())
    }

}

object Rotated : Drawer() {
    operator fun invoke(angle: Angle): Rotated {
        Drawer.angle = angle
        isRotationSet = true
        return this
    }

    fun about(pivotX: Double, pivotY: Double): Drawer {
        Drawer.pivotX = pivotX
        Drawer.pivotY = pivotY
        isPivotSet = true
        return this
    }
}

object ColorDrawer : Drawer() {
    operator fun invoke(hex: Color): ColorDrawer {
        colorArray[0] = hex.r.toFloat()
        colorArray[1] = hex.g.toFloat()
        colorArray[2] = hex.b.toFloat()
        colorArray[3] = hex.a.toFloat()
        return this
    }

    operator fun invoke(hex: Long): ColorDrawer {
        return invoke(hex(hex))
    }

}

abstract class Layout {
    var x: Double = 0.0
    var y: Double = 0.0
    var width: Double = 0.0
    var height: Double = 0.0

    abstract val outX: Double
    abstract val outY: Double
    abstract val outWidth: Double
    abstract val outHeight: Double
}

object Normal : Layout() {
    override val outX: Double
        get() = x
    override val outY: Double
        get() = y
    override val outWidth: Double
        get() = width
    override val outHeight: Double
        get() = height
}