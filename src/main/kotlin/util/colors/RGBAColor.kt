package util.colors

import util.units.Angle

class RGBAColor(red: Double, green: Double, blue: Double, alpha: Double = 1.0) : Color {

    override val r: Double = red
    override val g: Double = green
    override val b: Double = blue
    override val a: Double = alpha
    override val hue: Angle
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun toLong(): Long {
        return rgba2Hex(r,g,b,a)
    }

    override fun toString(): String {
        return "r:$r g:$g b:$b a:$a"
    }
}