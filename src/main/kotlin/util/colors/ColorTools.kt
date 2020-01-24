@file:Suppress("unused") // There will be many functions here that go unused most of the time

package util.colors

import util.units.Angle
import kotlin.math.max
import kotlin.math.min

fun hex(color: Long): Color = HexColor(color)

fun rgba(r: Double, g: Double, b: Double, a: Double): Color = HexColor(rgba2Hex(r,g,b,a))

fun Color.linearBlend(p: Double, other: Color): Color {
    val (r0, g0, b0, a0) = this
    val (r1, g1, b1, a1) = other

    fun blend(v0: Double, v1: Double, p: Double) = v0*(1-p)+v1*p

    val alpha = blend(a0, a1, p)
    val red = blend(r0, r1, p)
    val green = blend(g0, g1, p)
    val blue = blend(b0, b1, p)

    return HexColor(rgba2Hex(red, green, blue, alpha))
}

fun Color.logBlend(percent: Double, other: Color): Color {
    val p = percent%100.0
    val (r0, g0, b0, a0) = this
    val (r1, g1, b1, a1) = other

    fun Double.sqrd() = this*this
    fun Double.sqrt() = kotlin.math.sqrt(this)
    fun blend(v0: Double, v1: Double, p: Double) = (((1-p)*v0.sqrd()+p*v1.sqrd())).sqrt()

    val alpha= a0*(1-p)+a1*p
    val red = blend(r0, r1, p)
    val green = blend(g0, g1, p)
    val blue = blend(b0, b1, p)

    return HexColor(rgba2Hex(red, green, blue, alpha))
}

fun Color.lighten(amount: Int): Color {
    return HexColor(adjustShade(this.toLong(), amount))
}

fun Color.darken(amount: Int): Color {
    return HexColor(adjustShade(this.toLong(), -amount))
}

private fun adjustShade(hex: Long, amount: Int): Long {
    fun adjust(shift: Int): Long {
        val shifted = (hex shr shift and 0xFF)
        val willOverflow = amount > 0 && shifted > 0xFF - amount
        val willUnderflow = amount < 0 && shifted < 0x00 - amount
        return if (willOverflow) 0xFF else if (willUnderflow) 0x00 else shifted + amount
    }

    val r = adjust(24)
    val g = adjust(16)
    val b = adjust(8)
    val a = (hex and 0xFF)

    return a or (b shl 8) or (g shl 16) or (r shl 24)
}

fun rgba2Hex(red: Double, green: Double, blue: Double, alpha: Double): Long {
    val a = (alpha*255).toLong()
    val b = (blue*255).toLong() shl 8
    val g = (green*255).toLong() shl 16
    val r = (red*255).toLong() shl 24
    return r + b + g + a
}

fun hex2Alpha(hex: Long) = (hex and 0xFF)/255.0
fun hex2Blue(hex: Long) = (hex shr 8 and 0xFF)/255.0
fun hex2Green(hex: Long) = (hex shr 16 and 0xFF)/255.0
fun hex2Red(hex: Long) = (hex shr 24 and 0xFF)/255.0

fun hsl(hue: Angle, saturation: Double, lightness: Double, alpha: Double = 1.0): Color {
    fun f(n: Int, h: Double, l: Double, s: Double): Double {
        val a = s* min(l, 1.0-l)
        val k = (n + h/30.0)%12
        println("k: $k")
        return (l - a* max(min(min(k-3.0, 9.0-k), 1.0),-1.0)).also { println(it) }
    }

    val h = hue.toDegrees().asDouble()
    val s = saturation/100.0
    val l = lightness/100.0

    return rgba(f(0,h,s,l), f(8,h,s,l), f(4,h,s,l), alpha)
}

fun FloatArray.toColor(): Color {
    fun getComponent(i: Int) = if (this.size > i) this[i].toDouble() else 0.0
    val r = getComponent(0)
    val g = getComponent(1)
    val b = getComponent(2)
    val a = getComponent(3)
    return rgba(r,g,b,a)
}

operator fun Color.component1() = r
operator fun Color.component2() = g
operator fun Color.component3() = b
operator fun Color.component4() = a
