package util.colors

fun hex(color: Long): Color = HexColor(color)

fun rgba(r: Double, g: Double, b: Double, a: Double): Color = HexColor(rgba2Hex(r,g,b,a))

fun Color.linearBlend(p: Double, other: Color): Color {
    val (r0, g0, b0, a0) = this
    val (r1, g1, b1, a1) = other

    fun blend(v0: Double, v1: Double) = v0*(1-p)+v1*p

    val alpha = blend(a0, a1)
    val red = blend(r0, r1)
    val green = blend(g0, g1)
    val blue = blend(b0, b1)

    return HexColor(rgba2Hex(red, green, blue, alpha))
}

fun Color.logBlend(percent: Double, other: Color): Color {
    val p = percent%100.0
    val (r0, g0, b0, a0) = this
    val (r1, g1, b1, a1) = other

    fun Double.sqrd() = this*this
    fun Double.sqrt() = kotlin.math.sqrt(this)
    fun blend(v0: Double, v1: Double) = (((1-p)*v0.sqrd()+p*v1.sqrd())).sqrt()

    val alpha= a0*(1-p)+a1*p
    val red = blend(r0, r1)
    val green = blend(g0, g1)
    val blue = blend(b0, b1)

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

operator fun Color.component1() = r
operator fun Color.component2() = g
operator fun Color.component3() = b
operator fun Color.component4() = a
